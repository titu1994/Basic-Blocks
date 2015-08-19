package com.somshubra.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * File Cache 
 * Note: Doesnt work perfectly yet
 * @author Yue
 *
 */
public class FileCache {

	private String fileCacheName;
	private static long cacheSize = 4096;

	private int bytePointer;
	private byte[] cache;

	private FileManager fileManager;

	public FileCache(String cacheName) {
		this(cacheName, 4096);
	}

	public FileCache(String cacheName, int cacheSize) {
		this.fileCacheName = cacheName;
		FileCache.cacheSize = cacheSize;

		cache = new byte[cacheSize];
		fileManager = new FileManager(cacheName, cacheSize);
	}

	public void storeBytes(byte... data) {
		if (data.length > cacheSize)
			throw new UnsupportedOperationException(
					"Input data array cannot have size greater than cache size. Convert to fragments first!");

		int dataSize = data.length;
		if (bytePointer + dataSize <= cacheSize) {
			System.arraycopy(data, 0, cache, bytePointer, dataSize);
			bytePointer += dataSize;

			// Handle buffer filled
			if (bytePointer == cacheSize) {
				System.out.println("Invalidating cache");
				
				fileManager.addCache();
				fileManager.saveToCache(cache);
				bytePointer = 0;
				fileManager.addCache();
			}

		} else {
			System.out.println("Invalidating cache");
			fileManager.saveToCache(cache);
			bytePointer = 0;

			fileManager.addCache();

			System.arraycopy(data, 0, cache, bytePointer, data.length);
			bytePointer += dataSize;
		}
	}

	public byte[] getBytes(int cachenumber) {
		cache = fileManager.restoreFromFile(cachenumber);
		return cache;
	}

	public void releaseFileCache() {
		fileManager.deleteFileCache();
	}

	public void close() {
		fileManager.closeFiles();
	}

	@Override
	protected void finalize() throws Throwable {
		fileManager.closeFiles();
		super.finalize();
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public static byte[][] splitByteArrayToCacheFrames(byte[] data) {
		byte[][] splits = null;
		int n = (int) Math.ceil(data.length / (double) cacheSize);

		splits = new byte[n][(int) cacheSize];

		for (int remainder = data.length, i = 0, offset = 0; remainder > 0; remainder -= cacheSize, i++, offset += cacheSize) {
			System.arraycopy(data, offset, splits[i], 0,
					(int) (remainder > cacheSize ? cacheSize : remainder));
		}

		return splits;
	}

	private static class FileManager {
		private String fcName;
		private long cSize;
		private long setNo = 0;

		private static final String filetype = ".cache";
		private static final String metafiletype = ".meta";
		private static final int META_OFFSET = 2;

		private File cacheFile;
		private File metaFile;
		private ArrayList<String> metaLines;

		private BufferedOutputStream cacheWriter;
		private PrintWriter metaWriter;

		private ExecutorService executor;

		public FileManager(String fcName, long cSize) {
			this.fcName = fcName;
			this.cSize = cSize;

			cacheFile = new File(fcName + filetype);
			metaFile = new File(fcName + metafiletype);

			// Add meta info about filename and cache size
			try {
				metaLines = new ArrayList<>(Files.readAllLines(metaFile
						.toPath()));
			} catch (IOException e) {
			}

			if(metaLines == null)
				metaLines = new ArrayList<>();

			if (metaLines.isEmpty()) {
				loadFileReaderWriter(fcName, cSize);

				metaLines.add(fcName);
				metaLines.add(cSize + "");
			} else {
				int buffersize = Integer.parseInt(metaLines.get(1));
				if (cSize != buffersize) {
					loadFileReaderWriter(fcName, cSize);

					metaLines.clear();
					metaLines.add(fcName);
					metaLines.add(cSize + "");
					
				} else {
					try {
						cacheWriter = new BufferedOutputStream(
								new FileOutputStream(cacheFile, true));
						metaWriter = new PrintWriter(new BufferedWriter(new FileWriter(
								metaFile, true)), true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					setNo = Integer.parseInt(metaLines.get(metaLines.size() - 1).split(" ")[0]);
				}
			}

			executor = Executors.newCachedThreadPool();
		}

		private void loadFileReaderWriter(String fcName, long cSize) {
			try {
				cacheWriter = new BufferedOutputStream(new FileOutputStream(
						cacheFile, false));
				metaWriter = new PrintWriter(new BufferedWriter(new FileWriter(
						metaFile, false)), true);
			} catch (IOException e) {
				e.printStackTrace();
			}

			metaWriter.println(fcName);
			metaWriter.println(cSize + "");
		}

		public void saveToCache(byte[] cache) {
			executor.submit(() -> cacheToFile(cache));
		}

		public byte[] restoreFromFile(int cachenumber) {
			try {
				return executor.submit(() -> fileToCache(cachenumber)).get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new byte[(int) cSize];
		}

		public void addCache() {
			setNo++;
		}

		private synchronized void cacheToFile(byte[] cache) {
			// Add meta info about set number and the hash value for that set
			long hash = byteHash(cache);
			metaLines.add(setNo + " " + hash);
			metaWriter.println(setNo + " " + hash);

			// Append the cache to file
			try {
				cacheWriter.write(cache, 0, cache.length);
				cacheWriter.flush();
				System.out.println("Finished invalidating cache.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private synchronized byte[] fileToCache(int cachenumber) {
			byte[] data = new byte[(int) cSize];
			try {
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(cacheFile), (int) cSize);
				String hashline = metaLines.get(cachenumber + META_OFFSET);
				long h = Long.parseLong(hashline.split(" ")[1]);
				
				System.out.println(bis.markSupported());
				bis.mark(((int) cSize) * cachenumber);
				bis.read(data);
				long hash = byteHash(data);

				System.out.println("Finished loading cache.");
				if (h == hash)
					return data;
				else
					return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return data;
		}

		private long byteHash(byte[] cache) {
			long hash = 0;
			for (int i = 0; i < cache.length; i += 4)
				hash += cache[i];
			return hash;
		}

		public void closeFiles() {

			try {
				cacheWriter.flush();
				cacheWriter.close();
			} catch (IOException e) {

			}
			
			try {
				metaWriter.flush();
				metaWriter.close();
			}
			catch(Exception e) {}
			
			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
			}

			metaLines.clear();

			System.out.println("Cache and metadata file closed");
		}

		public void deleteFileCache() {
			if (cacheFile != null)
				cacheFile.delete();
			else {
				cacheFile = new File(fcName + filetype);
				cacheFile.delete();
			}

			if (metaFile != null)
				metaFile.delete();
			else {
				metaFile = new File(fcName + metafiletype);
				metaFile.delete();
			}
		}

	}

}

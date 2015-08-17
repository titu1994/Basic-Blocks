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

public class FileCache {

	private String fileCacheName;
	private long cacheSize = 4096;

	private int bytePointer;
	private byte[] cache;

	private FileManager fileManager;

	public FileCache(String cacheName) {
		this(cacheName, 4096);
	}

	public FileCache(String cacheName, int cacheSize) {
		this.fileCacheName = cacheName;
		this.cacheSize = cacheSize;

		cache = new byte[cacheSize];
		fileManager = new FileManager(cacheName, cacheSize);
	}

	public void storeBytes(byte... data) {
		if (data.length > cacheSize)
			throw new UnsupportedOperationException(
					"Input data array cannot have size greater than cache size");

		int dataSize = data.length;
		if (bytePointer + dataSize < cacheSize) {
			System.arraycopy(data, 0, cache, bytePointer, dataSize);
			bytePointer += dataSize;
		} else {
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

	@Override
	protected void finalize() throws Throwable {
		fileManager.closeFiles();
		super.finalize();
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

			try {
				cacheWriter = new BufferedOutputStream(new FileOutputStream(
						cacheFile, true));
				metaWriter = new PrintWriter(new BufferedWriter(new FileWriter(
						metaFile, true)), true);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Add meta info about filename and cachesize
			try {
				metaLines = new ArrayList<>(Files.readAllLines(metaFile
						.toPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (metaLines.isEmpty()) {
				metaWriter.println(fcName);
				metaWriter.println(cSize + "");

				metaLines.add(fcName);
				metaLines.add(cSize + "");
			}

			executor = Executors.newCachedThreadPool();
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

		private void cacheToFile(byte[] cache) {
			// Add meta info about set number and the hash value for that set
			long hash = byteHash(cache);
			metaLines.add(setNo + " " + hash);
			metaWriter.println(setNo + " " + hash);

			// Append the cache to file
			try {
				cacheWriter.write(cache, 0, cache.length);
				cacheWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private byte[] fileToCache(int cachenumber) {
			byte[] data = new byte[(int) cSize];
			try {
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(cacheFile));
				bis.read(data, ((int) cSize) * cachenumber, (int) cSize);
				long hash = byteHash(data);

				String hashline = metaLines.get(cachenumber + META_OFFSET);
				long h = Long.parseLong(hashline.split(" ")[1]);

				if (h == hash)
					return data;
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
				e.printStackTrace();
			}
			metaWriter.flush();
			metaWriter.close();

			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Cache and metadata file closed");
		}

	}

}

package com.somshubra.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelMergeSort {
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private static ExecutorService executor;
	private static long memory;
	private static int split1[], split2[];

	private ParallelMergeSort() {

	}
	
	public static void sort(int data[]) {
		if(NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}
		
		int sizePerThread = data.length / NO_OF_THREADS;
		ArrayList<Future<int[]>> list = new ArrayList<Future<int[]>>(NO_OF_THREADS);
		executor = Executors.newFixedThreadPool(NO_OF_THREADS);
		
		memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long arrayMemory = memory / (1024 * 1024);
		
		for(int i = 0; i < NO_OF_THREADS; i++) {
			list.add(executor.submit(createSplitArray(i, sizePerThread, data)));
		}
		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.gc();
		executor = Executors.newFixedThreadPool(NO_OF_THREADS);
		
		memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Before Horizontal Merge : Memory in MB : " + (memory/(1024*1024)));

		try {
			for(int i = 0; i < NO_OF_THREADS; i += 2) {
				split1 = list.remove(0).get();
				split2 = list.remove(0).get();
				
				list.add(executor.submit(createMergeArray(split1, split2)));
				
				split1 = null;
				split2 = null;
				System.gc();
			}
			
			list.trimToSize();
			System.gc();
			
			memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("After Horizontal Merge: Memory in MB : " + (memory/(1024*1024)));
	
			for(int i = list.size(); i > 1; i = list.size()) {
				split1 = list.remove(0).get();
				split2 = list.remove(0).get();
				
				list.add(executor.submit(createMergeArray(split1, split2)));
				
				split1 = null;
				split2 = null;
				System.gc();
			}
			
			memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("Final List : Memory in MB : " + (memory/(1024*1024)));
			
			list.trimToSize();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			
			split1 = list.remove(0).get();
			System.arraycopy(split1, 0, data, 0, data.length);
			split1 = null;
			
			list = null;

			long mergeMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024 * 1024);
			System.out.println("Memory Ratio : " + ((mergeMemory / (double) arrayMemory) - 1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		finally {
			System.gc();
		}
	}
	
	public static void sort(int data[], boolean cautious) {
		if(cautious) {
			if(data.length <= 2000000) {
				Arrays.sort(data);
				return;
			}
		}
		sort(data);
	}

	private static Callable<int[]> createSplitArray(int threadNo, int sizePerThread, int data[]) {
		Callable<int[]> r = new Callable<int[]>() {

			@Override
			public int[] call() throws Exception {
				int temp[];
				if(threadNo != (NO_OF_THREADS - 1)) {
					temp = Arrays.copyOfRange(data, sizePerThread * threadNo, sizePerThread * (threadNo + 1));
				}
				else {
					temp = Arrays.copyOfRange(data, sizePerThread * threadNo, data.length);
				}
				Arrays.sort(temp);
				return temp;
			}
		};
		return r;
	}

	private static Callable<int[]> createMergeArray(int a[], int b[]) {
		Callable<int[]> callable = new Callable<int[]>() {
			@Override
			public int[] call() throws Exception {
				int aLen = a.length;
				int bLen = b.length;
				int mergeArray[] = new int[a.length + b.length];
				int aPointe = 0, bPointe = 0, mergePointer = 0;
				
				while(aLen > 0 || bLen > 0) {
					if(aLen > 0 && bLen > 0) {
						while(aLen > 0 && bLen > 0) {
							if(a[aPointe] < b[bPointe]) {
								mergeArray[mergePointer] = a[aPointe];
								aPointe++;
								aLen--;
							}
							else {
								mergeArray[mergePointer] = b[bPointe];
								bPointe++;
								bLen--;
							}
							mergePointer++;
						}
					}
					else if(aLen > 0) {
						while(aLen > 0) {
							mergeArray[mergePointer] = a[aPointe];
							mergePointer++;
							aPointe++;
							aLen--;
						}
					}
					else if(bLen > 0) {
						while(bLen > 0) {
							mergeArray[mergePointer] = b[bPointe];
							mergePointer++;
							bPointe++;
							bLen--;
						}
					}
				}
				return mergeArray;
			}
		};
		return callable;
	}
	
	public static void setParallelism(int noOfThreads) {
		NO_OF_THREADS = noOfThreads;
	}
	
	public static void resetParallelism() {
		NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	}

}

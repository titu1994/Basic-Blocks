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

	private ParallelMergeSort() {

	}
	
	public static void sort(int data[]) {
		if(NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}
		
		int sizePerThread = data.length / NO_OF_THREADS;
		ArrayList<Future<int[]>> splitList = new ArrayList<Future<int[]>>(NO_OF_THREADS);
		executor = Executors.newFixedThreadPool(NO_OF_THREADS);
		
		memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long arrayMemory = memory / (1024 * 1024);
	
		for(int i = 0; i < NO_OF_THREADS; i++) {
			splitList.add(executor.submit(createSubArraySortCallable(i, sizePerThread, data)));
		}
		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Start Merge Threads
		executor = Executors.newFixedThreadPool(NO_OF_THREADS);

		try {
			for(int j = 0; j < NO_OF_THREADS; j += 2) {
				splitList.add(executor.submit(createMergedArrayCallable(splitList.remove(0).get(), splitList.remove(0).get())));
			}
			splitList.trimToSize();
	
			for(int i = splitList.size(); i > 1; i = splitList.size()) {
				splitList.add(executor.submit(createMergedArrayCallable(splitList.remove(0).get(), splitList.remove(0).get())));
				splitList.trimToSize();
			}
			
			System.gc();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.arraycopy(splitList.remove(0).get(), 0, data, 0, data.length);
			
			splitList = null;
			long mergeMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024 * 1024);
			System.out.println("Memory Ratio : " + (mergeMemory / (double) arrayMemory));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		finally {
			System.gc();
		}
	}

	private static Callable<int[]> createSubArraySortCallable(int threadNo, int sizePerThread, int data[]) {
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

	private static Callable<int[]> createMergedArrayCallable(int a[], int b[]) {
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

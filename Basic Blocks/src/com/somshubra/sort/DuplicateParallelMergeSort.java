package com.somshubra.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DuplicateParallelMergeSort {
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private static ExecutorService executor;
	private static long memory;

	private DuplicateParallelMergeSort() {
		// TODO Auto-generated constructor stub
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
		//System.out.println("Prior to Start : Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		
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

		//ArrayBlockingQueue<Future<int[]>> mergeList = new ArrayBlockingQueue<Future<int[]>>(splitList.size() / 2);

		try {
			
			/*memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("Prior to Merge Array : Memory in MB Consumed : " + (memory / (1024 * 1024)));
			*/
			for(int j = 0; j < NO_OF_THREADS; j += 2) {
				splitList.add(executor.submit(createMergedArrayCallable(splitList.remove(0).get(), splitList.remove(0).get())));
			}
			splitList.trimToSize();
	
			for(int i = splitList.size(); i > 1; i = splitList.size()) {
				splitList.add(executor.submit(createMergedArrayCallable(splitList.remove(0).get(), splitList.remove(0).get())));
			}
			splitList.trimToSize();
			System.gc();
		
			//memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			//System.out.println("Prior to Merge Array : Memory in MB Consumed : " + (memory / (1024 * 1024)));
			
			/*
			int start, end, setSize = 2 * sizePerThread;
			for(int j = 0; j < NO_OF_THREADS; j += 2) {
				start = (j/2) * setSize;
				if(j + 2 == NO_OF_THREADS) {
					end = data.length;
				}
				else {
					end = start + setSize;
				}
				mergeList.put(executor.submit(createMergeResultArrayCallable(data, start, end)));
			}
			
			splitList.clear();
			splitList = null;
			System.gc();
			
			setSize *= 2;
			start = 0; 
			end = start + setSize;
			boolean reset = false;
			System.out.println("Outer Set Size : " + setSize);
			System.out.println("Merge List Size : " + mergeList.size());
			
			for(int i = mergeList.size(); i > 1 || reset; i = mergeList.size()) {
				if(reset) {
					System.out.println("Entered Reset");
					setSize *= 2;
					System.out.println("Set Size : " + setSize);
					start = 0;
					if(checkLimit(start, setSize, data.length))
						end = data.length;
					else
						end = start + setSize;
					reset = false;
				}
				
				mergeList.put(executor.submit(createMergeResultArrayCallable(data, start, end)));
				mergeList.remove();
				mergeList.remove();
				
				start = end;
				if(checkLimit(start, setSize, data.length)) {
					end = data.length;
					reset = true;	
					System.out.println("Reset is now set");
				}
				else
					end = start + setSize;
			}
			*/
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
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
	
	private static boolean checkLimit(int start, int setSize, int dataLength) {
		if((start + setSize + 1) >= dataLength){
			System.out.println("Start + Set + 1 : " + (start + setSize + 1));
			return true;
		}
		else if((start + setSize - 1) == dataLength) {
			System.out.println("Start + Set + 1 : " + ((start + setSize - 1)));
			return true;
		}
		else if((start + setSize) == dataLength) {
			System.out.println("Start + Set + 1 : " + (start + setSize));
			return true;
		}
		else
			return false;
	}

	private static Callable<int[]> createSubArraySortCallable(int threadNo, int sizePerThread, int data[]) {

		Callable<int[]> r = new Callable<int[]>() {

			@Override
			public int[] call() throws Exception {
				int temp[];
				int start = 0, end = 0;
				//ystem.gc();
				
				if(threadNo != (NO_OF_THREADS - 1)) {
					temp = Arrays.copyOfRange(data, sizePerThread * threadNo, sizePerThread * (threadNo + 1));
					//start = sizePerThread * threadNo;
					//end = sizePerThread * (threadNo + 1);
				}
				else {
					temp = Arrays.copyOfRange(data, sizePerThread * threadNo, data.length);
					//start = sizePerThread * threadNo;
					//end = data.length;
				}
				
				/*memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Sub Array "+ threadNo + ": Memory in MB Consumed : " + (memory / (1024 * 1024)));
				*/
				
				Arrays.sort(temp);
				//Arrays.sort(data, start, end);
				
				//return data;
				return temp;
			}
		};

		return r;
	}

	private static Callable<int[]> createMergedArrayCallable(int a[], int b[]) {

		Callable<int[]> callable = new Callable<int[]>() {
			@Override
			public int[] call() throws Exception {
				//System.gc();
				
				int aLen = a.length;
				int bLen = b.length;
				int mergeArray[] = new int[a.length + b.length];
				
				memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Merge Callable : Memory in MB Consumed : " + (memory / (1024 * 1024)));
				
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
	
	private static Callable<int[]> createMergeResultArrayCallable(int data[], int start, int end) {

		Callable<int[]> callable = new Callable<int[]>() {
			@Override
			public int[] call() throws Exception {
				System.gc();
				Arrays.sort(data, start, end);
				return data;
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

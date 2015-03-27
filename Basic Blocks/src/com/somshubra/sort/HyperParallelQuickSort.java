package com.somshubra.sort;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HyperParallelQuickSort {
	
	private static int THRESHOLD = 1000000;
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private static ExecutorService executor;
	
	private static AtomicInteger counter = new AtomicInteger(1);
	private static Object lock = new Object();
	
	private HyperParallelQuickSort() {}

	public static void sort(int[] data) {
		if(NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}
		
		THRESHOLD = data.length / NO_OF_THREADS;
		counter.set(1);
		executor = Executors.newCachedThreadPool();
		
		counter.getAndIncrement();
		executor.submit(quickSortCallable(0, data.length - 1, data));
		
		synchronized (lock) {
			while(counter.get() > 1) {
				try {
					//System.out.println("Lock Count : " + counter.get());
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//System.gc();
		THRESHOLD = 1000000;
	}
	
	private static Runnable quickSortCallable(int low, int high, int data[]) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				 if((high - low) <= THRESHOLD) {
					Arrays.sort(data, low, high+1);
					
					synchronized (lock) {
						counter.getAndDecrement();
						lock.notify();
					}
					
					return;
				}
				 
				int i = low, j = high;
				int pivot = data[(low + high) >>> 1];
				int temp;
				
				while (i <= j) {
					IJPair ijPair = findIJ(data, pivot, i, j);
					i = ijPair.i;
					j = ijPair.j;

					if (i <= j) {
						temp = data[i];
						data[i] = data[j];
						data[j] = temp;
						i++;
						j--;
					}
				}
				
				if (low < j) {
					counter.getAndIncrement();
					executor.submit(quickSortCallable(low, j, data));
				}
				if (i < high) {
					counter.getAndIncrement();
					executor.submit(quickSortCallable(i, high, data));
				}
				
				synchronized (lock) {
					counter.getAndDecrement();
					lock.notify();
				}
			}
		};
		return r;
	}
	
	private static IJPair findIJ(int data[], int pivotElement, int i, int j) {
		counter.set(counter.get() + 2);
		Future<Integer> iVal = executor.submit(findI(data,pivotElement, i));
		Future<Integer> jVal = executor.submit(findJ(data,pivotElement, j));
		
		synchronized (lock) {
			counter.set(counter.get() - 2);
			lock.notify();
		}
		
		try {
			return new IJPair(iVal.get(), jVal.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Callable<Integer> findI(int data[], int pivotElement, int i) {
		Callable<Integer> c = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				int x = i;
				while (data[x] < pivotElement) {
					x++;
				}
				
				return x;
			}
		};
		return c;
	}
	
	private static Callable<Integer> findJ(int data[], int pivotElement, int j) {
		Callable<Integer> c = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				int x = j;
				while (data[x] > pivotElement) {
					x--;
				}
				
				return x;
			}
		};
		return c;
	}
	
	private static class IJPair {
		public final int i, j;
		
		public IJPair(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}
	
	public static void sort(int data[], boolean considerate) {
		if(considerate) {
			if(data.length <= THRESHOLD) {
				Arrays.sort(data);
				return;
			}
		}
		sort(data);
	}
	
	public static void setParallelism(int noOfThreads) {
		NO_OF_THREADS = noOfThreads;
	}
	
	public static void resetParralelism() {
		NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	}

}

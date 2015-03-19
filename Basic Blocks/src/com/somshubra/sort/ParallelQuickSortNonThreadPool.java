package com.somshubra.sort;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelQuickSortNonThreadPool {
	private static int THRESHOLD = 0;
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	
	private static AtomicInteger counter = new AtomicInteger(1);
	private static Object lock = new Object();

	private ParallelQuickSortNonThreadPool() {
	}

	public static void sort(int[] data) {
		if (NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}

		THRESHOLD = data.length / NO_OF_THREADS;
		counter.set(1);
		
		counter.getAndIncrement();
		Thread t = new Thread(quickSortCallable(data, 0, data.length - 1));
		t.start();
		
		synchronized (lock) {
			while (counter.get() > 1) {
				try {
					//System.out.println("Lock Count : " + counter.get());
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Runnable quickSortCallable(int data[], int low, int high) {
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
					while (data[i] < pivot) {
						i++;
					}
					
					while (data[j] > pivot) {
						j--;
					}

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
					new Thread(quickSortCallable(data, low, j)).start();
				}
				if (i < high) {
					counter.getAndIncrement();
					new Thread(quickSortCallable(data, i, high)).start();
				}
				
				synchronized (lock) {
					counter.getAndDecrement();
					lock.notify();
				}
			}
		};
		return r;
	}
	


	public static void sort(int data[], boolean considerate) {
		if (considerate) {
			if (data.length <= THRESHOLD) {
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

package com.somshubra.sort;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class PartitionParallelMergeSort {
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private static ForkJoinPool forkPool;
	private static int THRESHOLD = 1000000;
	
	private static AtomicInteger counter = new AtomicInteger(1);
	private static Object lock = new Object();
	
	private PartitionParallelMergeSort() {
		
	}
	
	public static void sort(int data[]) {
	/*	if(data.length <= THRESHOLD) {
			Arrays.sort(data);
			return;
		}
		*/
		
		if(NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}
		
		counter.set(1);
		
		forkPool = ForkJoinPool.commonPool();
		
		counter.getAndIncrement();
		forkPool.invoke(new ForkedTask(data));
		
		synchronized (lock) {
			while(counter.get() > 1) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		forkPool.shutdown();
		
		try {
			forkPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
		
	}
	
	private static class ForkedTask extends RecursiveAction {
		private static final long serialVersionUID = -3272010217443153547L;
		
		private int data[];
		private int low , high;
		
		public ForkedTask(int data[]) {
			this(data, 0, data.length);
		}
		
		public ForkedTask(int data[], int low, int high) {
			this.data = data;
			this.low = low;
			this.high = high;
		}
		
		@Override
		protected void compute() {
			if(high - low <= THRESHOLD) 
				computeDirectly();
			else {
				int mid = (high + low) >>> 1;
				
				counter.getAndIncrement();
				counter.getAndIncrement();
				invokeAll(new ForkedTask(data, low, mid), 
						  new ForkedTask(data, mid, high));
				/*forkPool.submit(new ForkedTask(data, low, mid));
				counter.getAndIncrement();
				forkPool.submit(new ForkedTask(data, mid, high));
				*/
				
				merge(low, mid, high);
			}
		}

		private void merge(int low, int mid, int high) {
			int temp[] = Arrays.copyOfRange(data, low, mid);
			int tLen = temp.length;
			int lowPointer = low;
			int midPointer = mid;
			for(int i = 0; i < tLen; lowPointer++) {
				if(midPointer == high || temp[i] < data[midPointer])
					data[lowPointer] = temp[i++];
				else
					data[lowPointer] = data[midPointer++];
			}
			
			synchronized (lock) {
				counter.getAndDecrement();
				lock.notify();
			}
			
			//System.out.println("Sorted Data : " + Arrays.toString(data));
			temp = null;
			//System.gc();
		}

		private void computeDirectly() {
			Arrays.sort(data, low, high);
			
			synchronized (lock) {
				counter.getAndDecrement();
				lock.notify();
			}
		}
		
	}

}

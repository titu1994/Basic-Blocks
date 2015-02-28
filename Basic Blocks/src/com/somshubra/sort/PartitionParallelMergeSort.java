package com.somshubra.sort;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PartitionParallelMergeSort {
	private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private static ForkJoinPool forkPool;
	
	private PartitionParallelMergeSort() {
		
	}
	
	public static void sort(int data[]) {
		if(NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}
		
		
	}
	
	private static class ForkedTask extends RecursiveAction {
		private int data[];
		public ForkedTask(int data[]) {
			this.data = data;
		}
		
		@Override
		protected void compute() {
			//Arrays.sort
		}

		private void computeDirectly() {
			
		}
		
		
	}

}

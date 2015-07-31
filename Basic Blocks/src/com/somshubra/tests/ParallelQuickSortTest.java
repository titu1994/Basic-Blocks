package com.somshubra.tests;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.somshubra.sort.ParallelQuickSort;
public class ParallelQuickSortTest {

	public static void main(String[] args) {
		Random r = new Random();
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter the no of elements : ");
		int n = sc.nextInt();
		
		if(n <= 0)
		{
			System.out.println("Enter a valid number");
			return;
		}
		Runtime runtime = Runtime.getRuntime();
		
		int data[] = new int[n];
		//int data2[] = new int[n];
		for(int i = 0; i < n; i++) {
			data[i] = r.nextInt(n+1);
		//	data2[i] = data[i];
		}
		
		//System.out.println("Unsorted Data : " + Arrays.toString(data));
		
		long memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		//To test on sorted Data
		//Arrays.sort(data);
		
		long begin = System.currentTimeMillis();
		//ParallelMergeSort.setParallelism(4);
		ParallelQuickSort.sort(data);
		long end = System.currentTimeMillis();
		
		//System.out.println("Sorted Data : " + Arrays.toString(data));
		
		long diff = end - begin;
		long time1 = diff;
		//System.out.println("Is Sorted : " + isSorted(data));
		memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memory in MB Consumed After execution: " + (memory / (1024 * 1024)));
		System.out.println("ParallelQuickSort.sort() Millitime : " + diff);
		
		runtime.gc();
		
		
		for(int i = 0; i < n; i++) {
			data[i] = r.nextInt(n+1);
		}
		
		
		begin = System.currentTimeMillis();
		Arrays.parallelSort(data);
		//Arrays.sort(data);
		end = System.currentTimeMillis();
		
		//memory = runtime.totalMemory() - runtime.freeMemory();
		//System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		diff = end - begin;
		long time2 = diff;
		
		System.out.println("Arrays.Sort() Millitime : " + diff);
		System.out.println("Gain (in ms) : " + (time2 - time1));
		System.out.println("Percentage gain : " + ((time2 - time1) / (double)time1) * 100 + " %");
	}
	
	private static boolean isSorted(int data[]) {
		boolean sorted = true;
		for(int i = 0; i < data.length - 1; i++) {
			if(data[i] > data[i+1])
				return false;
		}
		return sorted;
	}

}

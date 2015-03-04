package com.somshubra.tests;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.somshubra.sort.ParallelMergeSort;

public class ParallelMergeSortTest {

	public static void main(String[] args) {
		Random r = new Random();
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter the no of elements : ");
		int n = sc.nextInt();
		
		Runtime runtime = Runtime.getRuntime();
		
		int data[] = new int[n];
		for(int i = 0; i < n; i++) {
			data[i] = r.nextInt(n+1);
		}
		
		//System.out.println("Unsorted Data : " + Arrays.toString(data));
		
		long memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		long begin = System.currentTimeMillis();
		//ParallelMergeSort.setParallelism(4);
		//ParallelMergeSort.sort(data);
		long end = System.currentTimeMillis();
		
		//System.out.println("Sorted Data : " + Arrays.toString(data));
		//memory = runtime.totalMemory() - runtime.freeMemory();
		//System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		long diff = end - begin;
		long time1 = diff;
		System.out.println("Is Sorted : " + isSorted(data));
		System.out.println("ParallelMergeSort.sort() Millitime : " + diff);
		
		runtime.gc();
		
		for(int i = 0; i < n; i++) {
			data[i] = r.nextInt(n+1);
		}
		
		begin = System.currentTimeMillis();
		//Arrays.parallelSort(data);
		Arrays.sort(data);
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
/*
 * https://courses.cs.washington.edu/courses/cse373/13wi/lectures/03-13/MergeSort.java
 * original:
   1024000 elements  =>     187 ms 
   2048000 elements  =>     378 ms 
   4096000 elements  =>     810 ms 
   8192000 elements  =>    1636 ms 
  16384000 elements  =>    3369 ms 
  32768000 elements  =>    6751 ms 

parallel (2 threads):
   1024000 elements  =>     110 ms 
   2048000 elements  =>     251 ms 
   4096000 elements  =>     458 ms 
   8192000 elements  =>     952 ms 
  16384000 elements  =>    1860 ms 
  32768000 elements  =>    3910 ms 

parallel (4 threads):
   1024000 elements  =>      82 ms 
   2048000 elements  =>     166 ms 
   4096000 elements  =>     342 ms 
   8192000 elements  =>     674 ms 
  16384000 elements  =>    1240 ms 
  32768000 elements  =>    2668 ms 
*/

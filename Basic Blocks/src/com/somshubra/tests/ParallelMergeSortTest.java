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
		ParallelMergeSort.sort(data);
		long end = System.currentTimeMillis();
		
		//System.out.println("Sorted Data : " + Arrays.toString(data));
		memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		long diff = end - begin;
		System.out.println("Millitime : " + diff);
		
		runtime.gc();
		
		for(int i = 0; i < n; i++) {
			data[i] = r.nextInt(n+1);
		}
		
		begin = System.currentTimeMillis();
		Arrays.sort(data);
		end = System.currentTimeMillis();
		
		memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memory in MB Consumed : " + (memory / (1024 * 1024)));
		
		diff = end - begin;
		System.out.println("Millitime : " + diff);
	}

}

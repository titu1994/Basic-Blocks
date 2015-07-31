package com.somshubra.tests.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.somshubra.sort.ParallelQuickSort;

public class JavaSortingAnalysis {

	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
		Random r = new Random();
		Scanner sc = new Scanner(System.in);
		Runtime runtime = Runtime.getRuntime();

		System.out.print("Enter the no of elements : ");
		int n = sc.nextInt();

		System.out.print("Enter the no of tests to perform : ");
		int setSize = sc.nextInt();

		System.out.println("Enter 1 for Arrays.sort() and 2 for Arrays.parallelSort()");
		int choice = sc.nextInt();

		if(choice != 1 && choice != 2) {
			System.out.println("Please choose between 1 and 2");
			return;
		}

		if (n <= 0) {
			System.out.println("Enter a valid number");
			return;
		}
		
		File output = new File("Java Result " + n + " Type " + choice + ".json");
		FileWriter fw = new FileWriter(output, true);
		PrintWriter pw = new PrintWriter(new BufferedWriter(fw), true);

		for(int x = 0; x < setSize; x++) {
			Result result = new Result();
			result.arraySize = n;
			result.sortType = choice;

			int data[] = new int[n];
			for (int i = 0; i < n; i++) {
				data[i] = r.nextInt(n + 1);
			}

			long memory = runtime.totalMemory() - runtime.freeMemory();
			result.totalMemoryConsumed = memory / (1024 * 1024);

			long begin = 0, end = 0;
			runtime.gc();

			try {
				begin = System.currentTimeMillis();
				if(choice == 1)
					Arrays.sort(data);
				else
					Arrays.parallelSort(data);
				end = System.currentTimeMillis();
			} catch (OutOfMemoryError e) {
				
			}
			long diff = end - begin;
			result.arraySortTotalTime = diff;
			
			pw.append(gson.toJson(result));
			pw.println();
			System.gc();
		}
		
		pw.close();
		System.out.println("Results have been written.");
	}

	private static boolean isSorted(int data[]) {
		boolean sorted = true;
		for (int i = 0; i < data.length - 1; i++) {
			if (data[i] > data[i + 1])
				return false;
		}
		return sorted;
	}

	public static class Result {
		public int arraySize;
		public int sortType = 1;
		public long arraySortTotalTime;
		public long totalMemoryConsumed;
	}

}

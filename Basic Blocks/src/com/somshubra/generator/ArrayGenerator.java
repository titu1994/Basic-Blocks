package com.somshubra.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class ArrayGenerator {
	private int n, k;
	private int array[];
	private Random random;

	private static int BUFFER_SIZE = 8 * 1024 * 1024;

	public ArrayGenerator(int n, int k) {
		this.n = n;
		this.k = k;
		this.array = new int[n];
		random = new Random();
	}
	
	public ArrayGenerator generateKArrays() {
		for(int i = 0; i < k; i++) {
			randomize();
			writeToFile();
			System.out.print(".");
			System.gc();
		}
		System.out.println();
		return this;
	}

	private void randomize() {
		for(int i = 0; i < n; i++) {
			array[i] = random.nextInt(32767);
		}
	}

	private void writeToFile() {
		String filename = "Array-" + n + ".txt";
		File f = new File(filename);
		try(PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(f, true)), true)) {
			String holder = Arrays.toString(array);
			holder = holder.substring(1, holder.length()-1);
			pr.println(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package com.somshubra.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ArrayGenerator {
	private int n, k;
	private int array[];
	private Random random;
	private int maxIntegerValue = 32767;
	
	public static final int GENERATOR_MODE_RANDOM = 1;
	public static final int GENERATOR_MODE_ALMOST_SORTED = 2;
	public static final int GENERATOR_MODE_REVERSE_SORTED = 3;
	private static double K_SORTED = 0.1;

	public ArrayGenerator(int n, int k) {
		this(n, k, 32767);
	}
	
	public ArrayGenerator(int n, int k, int maxIntegerValue) {
		this.n = n;
		this.k = k;
		this.maxIntegerValue = maxIntegerValue;
		this.array = new int[n];
		random = new Random();
	}
	
	public ArrayGenerator generateKArrays(int generatorMode) {
		if(generatorMode < GENERATOR_MODE_RANDOM || generatorMode > GENERATOR_MODE_REVERSE_SORTED)
			generatorMode = GENERATOR_MODE_RANDOM;
		
		for(int i = 0; i < k; i++) {
			randomize(generatorMode);
			writeToFile(generatorMode);
			System.gc();
		}
		return this;
	}
	
	public ArrayGenerator generateKArrays(int generatorMode, double kSorted) {
		if(generatorMode < GENERATOR_MODE_RANDOM || generatorMode > GENERATOR_MODE_REVERSE_SORTED)
			generatorMode = GENERATOR_MODE_RANDOM;
		
		if(kSorted >= 1.0)
			kSorted = 1.0;
		else if(kSorted <= 0.01)
			kSorted = 0.01;
		
		ArrayGenerator.K_SORTED = kSorted;
		
		for(int i = 0; i < k; i++) {
			randomize(generatorMode, kSorted);
			writeToFile(generatorMode);
			System.gc();
		}
		return this;
	}

	private void randomize(int mode) {
		switch(mode) {
		case GENERATOR_MODE_RANDOM: {
			for(int i = 0; i < n; i++) {
				array[i] = random.nextInt(maxIntegerValue);
			}
			break;
		}
		case GENERATOR_MODE_ALMOST_SORTED: {
			randomize(mode, K_SORTED);
			break;
		}
		case GENERATOR_MODE_REVERSE_SORTED: {
			for(int i = 0; i < n; i++) {
				array[i] = random.nextInt(maxIntegerValue);
			}
			Arrays.sort(array);
			
			int temp = 0, n = array.length;
			for(int i = 0; i < n / 2; i++) {
				temp = array[i];
				array[i] = array[n - i - 1];
				array[n - i - 1] = temp;
			}
			break;
		}
		}
	}
	
	private void randomize(int mode, double k) {
		for(int i = 0; i < n; i++) {
			array[i] = random.nextInt(maxIntegerValue);
		}
		
		int levels = (int) (array.length * k);
		for(int i = 0; i < array.length; i += levels) {
			if(i + levels >= array.length) 
				Arrays.sort(array, i, array.length);
			else 
				Arrays.sort(array, i, i + levels);
		}
	}

	private void writeToFile(int mode) {
		String filename = "Array-" + n + "-";
		if(mode == GENERATOR_MODE_RANDOM)
			filename += "Random.txt";
		else if(mode == GENERATOR_MODE_ALMOST_SORTED)
			filename += "Almost-Sorted-" + K_SORTED +".txt";
		else if(mode == GENERATOR_MODE_REVERSE_SORTED)
			filename += "Reverse-Sorted.txt";
		
		File f = new File(filename);
		try(PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(f, true)), true)) {
			String holder = Arrays.toString(array);
			pr.println(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

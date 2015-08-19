package com.somshubra.tests;

import java.util.Scanner;

import com.somshubra.generator.ArrayGenerator;

public class KArrayGeneration {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter n and k : ");
		int n = sc.nextInt();
		int k = sc.nextInt();
		System.out.println("Enter mode : 1 for Random, 2 for Almost Sorted, 3 for Reverse Sorted");
		int choice = sc.nextInt();

		ArrayGenerator ag = new ArrayGenerator(n, k);
		long t1,t2;
		switch (choice) {
		case 1:
			t1 = System.currentTimeMillis();
			ag.generateKArrays(ArrayGenerator.GENERATOR_MODE_RANDOM);
			t2 = System.currentTimeMillis();
			break;
		case 2:
			System.out.print("Enter sorted % (0.0 - 1.0) : ");
			double sn = sc.nextDouble();
			t1 = System.currentTimeMillis();
			ag.generateKArrays(ArrayGenerator.GENERATOR_MODE_ALMOST_SORTED, sn);
			t2 = System.currentTimeMillis();
			break;
		case 3:
			t1 = System.currentTimeMillis();
			ag.generateKArrays(ArrayGenerator.GENERATOR_MODE_REVERSE_SORTED);
			t2 = System.currentTimeMillis();
			break;
		default:
			t1 = System.currentTimeMillis();
			ag.generateKArrays(ArrayGenerator.GENERATOR_MODE_RANDOM);
			t2 = System.currentTimeMillis();
			break;
		}
		
		System.out.println("Time in milliseconds : " + (t2 - t1));
		System.out.println("File writing finished.");
	}

}

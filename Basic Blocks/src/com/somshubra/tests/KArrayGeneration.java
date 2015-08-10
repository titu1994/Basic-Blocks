package com.somshubra.tests;

import java.util.Scanner;

import com.somshubra.generator.ArrayGenerator;

public class KArrayGeneration {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter n and k : ");
		ArrayGenerator ag = new ArrayGenerator(sc.nextInt(), sc.nextInt())
								.generateKArrays();
		System.out.println("File writing finished.");
	}

}

package com.somshubra.tests;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.somshubra.random.TrueRandom;

public class TrueRandomTest {
	private static final TrueRandom random = new TrueRandom();

	private static int positive = 0, negative = 0;

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter no of iterations");

		int n = sc.nextInt();
		ResultWriter writer = new ResultWriter("TrueRandomTest");;

		for(int k = 0; k < 1000; k++) {
			for(int i = 0; i < n; i++) {
				if(random.nextBoolean()) 
					positive++;
				else
					negative++;
			}

			//System.out.println("Positive / Negative ratio : " + positive/(double)negative);
			//System.out.println("Positive - Negative : " + (positive - negative) );

			writer.write("" + positive/(double)negative);
			writer.write("" + (positive - negative));
		}
		writer.closeWriter();
	}
}

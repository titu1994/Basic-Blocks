package com.somshubra.tests;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.somshubra.cache.FileCache;

public class FileCacheTest {

	private static final Random random = new Random();
	
	public static void main(String[] args) {
		FileCache cacheManager = new FileCache("random-cache-1", 8);
		int cacheSize = (int) cacheManager.getCacheSize();
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter no of cache : ");
		int n = sc.nextInt();
		
		byte[] data = new byte[cacheSize * n];
		random.nextBytes(data);

		System.out.println("Data array : " + Arrays.toString(data));
		
		byte[][] fragments = FileCache.splitByteArrayToCacheFrames(data);
		
		if(fragments.length > 1) 
			System.out.println("Multiple fragments created.");
		else
			System.out.println("Single fragment created.");
		
		for(int i = 0; i < fragments.length; i++) 
			cacheManager.storeBytes(fragments[i]);
		
		byte[] result = cacheManager.getBytes(0);
		System.out.println("Result Array : " + Arrays.toString(result));
		
		boolean isSame = true;
		for(int i = 0, offset = 0; i < cacheSize; i++) {
			if(data[i + offset] != result[i]) {
				isSame = false;
				break;
			}
		}
		
		System.out.println("Is Same : " + isSame);
		cacheManager.close();
	}

}

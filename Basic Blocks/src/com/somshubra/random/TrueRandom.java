package com.somshubra.random;

import java.util.Random;

public class TrueRandom {
	
	private Random random;
	private int level;
	
	public TrueRandom() {
		level = 3;
		initialize();
	}
	
	public TrueRandom(int level) {
		this.level = level;
		initialize();
	}

	private void initialize() {
		Random r = new Random();
		
		random = new Random(r.nextLong());
		for(int i = 1; i < level; i++) {
			random = new Random(random.nextLong());
		}
	}

	public boolean nextBoolean() {
		double gaussian = 0;
		
		for(int i = 0; i < level; i++) {
			gaussian = random.nextGaussian();
		}
		
		return gaussian >= random.nextGaussian() ? true : false;
	}

	
	public void nextBytes(byte[] bytes) {
		for(int i = 0; i < level; i++)
			random.nextBytes(bytes);
	}

	public double nextDouble() {
		double d = 0;
		for(int i = 0; i < level; i++) 
			d = random.nextDouble();
		
		return d;
	}

	public float nextFloat() {
		float f = 0;
		for(int i = 0; i < level; i++) 
			f = random.nextFloat();
		return f;
	}

	public synchronized double nextGaussian() {
		double gauss = 0;
		for(int i = 0; i < level; i++) 
			gauss = random.nextGaussian();
			
		return gauss;
	}

	public int nextInt() {
		int v = 0;
		for(int i = 0; i < level; i++) 
			v = random.nextInt();
		return v;
	}

	public int nextInt(int n) {
		int v = 0;
		for(int i = 0; i < level; i++) 
			v = random.nextInt(n);
		return v;
	}

	public long nextLong() {
		long l = 0;
		for(int i = 0; i < level; i++)
			l = random.nextLong();
		return l;
	}
	
}

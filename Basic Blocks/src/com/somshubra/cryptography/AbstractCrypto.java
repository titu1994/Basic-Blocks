package com.somshubra.cryptography;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public abstract class AbstractCrypto {
	private HashMap<String, Integer> encryptMap;
	private HashMap<Integer, String> decryptMap;
	
	private Random random;
	public static final int MAX_SET_VAL = 999;
	public static final int MIN_SET_VAL = 1;

	public AbstractCrypto() {
		encryptMap = new HashMap<String, Integer>();
		decryptMap = new HashMap<Integer, String>();
		random = new Random();
		
		loadEncryptMap();
		loadDecryptMap();
	}

	protected abstract void loadEncryptMap();
	
	protected void addEncryptMapKeyValPair(char key) {
		encryptMap.put(key + "", getNextRandom());
	}

	private void loadDecryptMap() {
		if(encryptMap != null && decryptMap != null) {
			Set<Entry<String, Integer>> set = encryptMap.entrySet();
			for(Entry<String, Integer> entry : set) {
				decryptMap.put(entry.getValue(), entry.getKey());
			}
		}
	}

	protected String encrypt(String input) {
		StringBuffer output = new StringBuffer();
		int mappedVal = 0;

		for(int i = 0; i < input.length(); i++) {
			mappedVal = encryptMap.get(input.charAt(i) + "");

			if(mappedVal >= 0 && mappedVal <= 9) {
				output.append("00" + mappedVal);
			}
			else if(mappedVal >= 10 && mappedVal <= 99) {
				output.append("0" + mappedVal);
			}
			else {
				output.append(mappedVal);
			}
		}

		return output.toString();
	}

	protected String decrypt(String input) {
		StringBuffer output = new StringBuffer();
		String mappedVal = "";
		String buffer = "";

		for(int i = 0; i < input.length(); i += 3) {
			buffer = input.charAt(i) + "";
			buffer = buffer + input.charAt(i+1);
			buffer = buffer + input.charAt(i+2);
			
			if(buffer.charAt(0) == '0' && buffer.charAt(1) == '0') {
				mappedVal = decryptMap.get(Integer.parseInt(buffer.charAt(2) + ""));
			}
			else if(buffer.charAt(0) == '0') {
				mappedVal = decryptMap.get(Integer.parseInt(buffer.charAt(1) + "" + buffer.charAt(2)));
			}
			else {
				mappedVal = decryptMap.get(Integer.parseInt("" + buffer.charAt(0) + "" + buffer.charAt(1) + "" + buffer.charAt(2)));
			}
			
			output.append(mappedVal);
		}

		return output.toString();
	}
	
	protected int getNextRandom() {
		int rand = random.nextInt(MAX_SET_VAL - MIN_SET_VAL + 1) + MIN_SET_VAL;
		while(rand >= MIN_SET_VAL && rand <= MAX_SET_VAL && encryptMap.containsValue(rand)) {
			rand = random.nextInt(MAX_SET_VAL - MIN_SET_VAL + 1) + MIN_SET_VAL;
		}
		
		return rand;
	}

}

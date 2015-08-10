package com.somshubra.cryptography;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleCrypto extends AbstractCrypto {

	@Override
	protected void loadEncryptMap() {
		for(int i = 0; i < 256; i++) {
			addEncryptMapKeyValPair((char) i);
		}
	}
	
	public static void main(String args[]) throws IOException {
		BufferedReader bb = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter a string : ");
		String input = bb.readLine();
		
		SimpleCrypto crypto = new SimpleCrypto();
		//crypto.printEncryptionMap();
		
		String encryptedString = crypto.encrypt(input);
		System.out.println("Encrypted String : " + encryptedString);
		
		String decryptedString = crypto.decrypt(encryptedString);
		System.out.println("Decrypted String : " + decryptedString);
		
		
	}
	
}

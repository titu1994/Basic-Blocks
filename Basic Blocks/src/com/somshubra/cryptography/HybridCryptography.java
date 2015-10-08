package com.somshubra.cryptography;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.crypto.KeyGenerator;

public class HybridCryptography {
	
	public static final int KEYSIZE = 128;
	
	private byte[] stringToBytes(String ip) {
		try {
			return ip.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String bytesToBase64(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	private byte[] base64ToBytes(String data) {
		return Base64.getDecoder().decode(data);
	}
	
	private String bytesToString(byte[] ip) {
		return new String(ip);
	}

	private byte[] bitwiseXOR(byte[] x1, byte[] x2) {
		byte data[] = null, tempX[] = null;
		int lenX1 = x1.length;
		int lenX2 = x2.length;
		
		if(lenX1 != lenX2) {
			int g = Math.max(lenX1, lenX2);
			tempX = new byte[g];
			
			if(lenX1 != g) {
				int offset = g - lenX1;
				System.arraycopy(x1, offset, tempX, 0, x1.length);
				x1 = tempX;
			}
			else {
				int offset = g - lenX2;
				System.arraycopy(x2, offset, tempX, 0, x2.length);
				x2 = tempX;
			}
		}
		
		data = new byte[x1.length];
		
		
		return data;
	}
	
}

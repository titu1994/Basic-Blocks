package com.somshubra.serialization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Deserialization {

	public static void main(String[] args) throws IOException {
		File input = new File("test/Entity.txt");
		
		ArrayList<Entity> list = null;
		Gson gson = new Gson();
		
		BufferedReader bb = new BufferedReader(new FileReader(input));
		
		String buffer = bb.readLine();
		bb.close();
		
		list = gson.fromJson(buffer, new TypeToken<ArrayList<Entity>>(){}.getType());
		for(Entity e : list) {
			System.out.println(e);
		}
		
	}

}

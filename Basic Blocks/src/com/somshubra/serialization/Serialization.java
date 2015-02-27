package com.somshubra.serialization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Serialization {

	public static void main(String[] args) throws IOException {
		File output = new File("test/Entity.txt");
		output.mkdirs();
		if(!output.exists())
			output.createNewFile();
		else {
			output.delete();
			output.createNewFile();
		}
		ArrayList<String> x = new ArrayList<>();
		x.add("X");
		Entity a = new Entity("A", 1, x);
		Entity b = new Entity("B", 2, x);
		Entity c = new Entity("c", 3, x);
		Entity d = new Entity("2", 20, x);
		Entity e = new Entity("@", 15, x);
		
		ArrayList<Entity> list = new ArrayList<Entity>();
		list.add(a);
		list.add(b);
		list.add(c);
		list.add(d);
		list.add(e);
		
		Gson gson = new Gson();
		String json = gson.toJson(list);
		
		FileWriter w = new FileWriter(output);
		PrintWriter pw = new PrintWriter(new BufferedWriter(w), true);
		
		pw.println(json);
		pw.close();
		
	}

}

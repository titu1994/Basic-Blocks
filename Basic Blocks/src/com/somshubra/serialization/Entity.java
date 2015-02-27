package com.somshubra.serialization;

import java.util.ArrayList;

public class Entity {
	
	public final String name;
	public final int id;
	public ArrayList<String> x;
	
	public Entity(String name, int id, ArrayList<String> x) {
		this.name = name;
		this.id = id;
		this.x = x;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Name : " + name + " ID : " + id + "x : " + x.get(0);
	}

}

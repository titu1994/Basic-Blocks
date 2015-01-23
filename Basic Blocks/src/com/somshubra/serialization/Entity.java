package com.somshubra.serialization;

public class Entity {
	
	public final String name;
	public final int id;
	
	public Entity(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Name : " + name + " ID : " + id;
	}

}

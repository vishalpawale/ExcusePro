package com.sudosaints.excusepro.model;

import java.io.Serializable;

/**
 * 
 * @author Vishal
 *
 */
public class Category implements Serializable{

	String name;
	
	Long id;

	public Category() {
		
	}
	
	public Category(String name, Long id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}

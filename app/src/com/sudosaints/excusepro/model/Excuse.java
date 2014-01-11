package com.sudosaints.excusepro.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Vishal
 *
 */
public class Excuse implements Serializable{

	String excuse;
	Date date;
	Long id;
	
	public Excuse(String excuse, Date date, Long id) {
		super();
		this.excuse = excuse;
		this.date = date;
		this.id = id;
	}

	public Excuse() {
	}

	public String getExcuse() {
		return excuse;
	}
	
	public void setExcuse(String excuse) {
		this.excuse = excuse;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
}

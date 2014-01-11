package com.sudosaints.excusepro.model;

import java.io.Serializable;

/**
 * 
 * @author Vishal
 *
 */
public class ShareObject implements Serializable{

	String name, caption, message, description, link, picture;

	public ShareObject(String name, String caption, String message,
			String description, String link, String picture) {
		super();
		this.name = name;
		this.caption = caption;
		this.message = message;
		this.description = description;
		this.link = link;
		this.picture = picture;
	}

	public ShareObject(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
}

package com.sudosaints.excusepro;

import java.util.List;

import com.sudosaints.excusepro.model.Category;

/**
 * 
 * @author Vishal
 *
 */
public class DataCache {

	private static List<Category> categories;

	public static List<Category> getCategories() {
		return categories;
	}

	public static void setCategories(List<Category> categories) {
		DataCache.categories = categories;
	}
	
}

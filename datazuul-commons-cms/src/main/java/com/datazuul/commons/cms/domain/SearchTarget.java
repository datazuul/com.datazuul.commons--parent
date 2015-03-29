package com.datazuul.commons.cms.domain;

import java.io.Serializable;

public class SearchTarget implements Serializable {
	public static final int AUTHORS_AND_TITLES = 0;
	public static final int CATEGORIES_AND_ARTICLES = 1;

	// TODO use Enumeration instead...
	int id;
	String label;

	public SearchTarget(int id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return label;
	}
}

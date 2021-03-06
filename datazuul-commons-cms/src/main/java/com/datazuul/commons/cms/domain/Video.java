package com.datazuul.commons.cms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video implements Serializable {
	private long id = 0;

	private Author author;

	private List categories = new ArrayList();

	private VideoProperties properties = new VideoProperties("properties");

	private String description;

	private String format;

	private String title;

	public Video(String title, byte[] bytes, String format) {
		this.format = format;
		this.title = title;

		properties.setBytes(bytes);
	}

	public Video(long id) {
		this.id = id;
	}

	public Video() {
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isNew() {
		return (id < 1);
	}

	public VideoProperties getProperties() {
		return properties;
	}

	public void setProperties(VideoProperties properties) {
		this.properties = properties;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List getCategories() {
		return categories;
	}

	public void setCategories(List categories) {
		this.categories = categories;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}

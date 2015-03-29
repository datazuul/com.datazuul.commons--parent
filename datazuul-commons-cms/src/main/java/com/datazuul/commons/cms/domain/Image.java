package com.datazuul.commons.cms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Image implements Serializable {
	private long id = 0;

	public ImageProperties propsOriginal = new ImageProperties("original");

	public ImageProperties propsPreview = new ImageProperties("preview");

	public ImageProperties propsThumbnail = new ImageProperties("thumbnail");

	private Author author;

	private List categories = new ArrayList();

	private String align;

	private String format;

	private String title;

	private String valign;

	private String description;

	public Image(byte[] bytes, String format) {
		propsOriginal.setBytes(bytes);
		this.format = format;
	}

	public Image(long id) {
		this.id = id;
	}

	public Image() {
	}

	public Author getAuthor() {
		return author;
	}

	public List getCategories() {
		return categories;
	}

	public void setCategories(List categories) {
		this.categories = categories;
	}

	public long getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isNew() {
		return (id < 1);
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

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getValign() {
		return valign;
	}

	public void setValign(String valign) {
		this.valign = valign;
	}

	public ImageProperties getPropsOriginal() {
		return propsOriginal;
	}

	public void setPropsOriginal(ImageProperties propsOriginal) {
		this.propsOriginal = propsOriginal;
	}

	public ImageProperties getPropsPreview() {
		return propsPreview;
	}

	public void setPropsPreview(ImageProperties propsPreview) {
		this.propsPreview = propsPreview;
	}

	public ImageProperties getPropsThumbnail() {
		return propsThumbnail;
	}

	public void setPropsThumbnail(ImageProperties propsThumbnail) {
		this.propsThumbnail = propsThumbnail;
	}
}

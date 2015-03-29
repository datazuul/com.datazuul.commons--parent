package com.datazuul.commons.cms.domain;

import java.io.Serializable;

/**
 * @author ralf
 * 
 * @hibernate.class table="ARTICLES"
 */
public class Article implements Serializable {
	private long id = 0;

	private String title = null;

	private String htmlContent = null;

	private Author author = null;

	private Category category = null;

	public Article() {

	}

	public Article(long id) {
		this.id = id;
	}

	public boolean isNew() {
		return (id < 1);
	}

	/**
	 * @return the author
	 * @hibernate.component
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the htmlContent
	 * @hibernate.property column="HTML"
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * @param htmlContent
	 *            the htmlContent to set
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/**
	 * @return the id
	 * @hibernate.id column="ID" generator-class="increment" unsaved-value="0"
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	public String toString() {
		return ("[id=" + this.id + ", htmlContent=" + this.htmlContent + "]");
	}

	/**
	 * @return the title
	 * @hibernate.property column="TITLE"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the parent category
	 * @hibernate.many-to-one cascade="none" column="CATEGORY"
	 */
	/*
	 * @hibernate.many-to-one: The relation is modeled in the database with a
	 * foreign key in the ARTICLES table holding the primary key of the parent
	 * category in it from the CATEGORIES table.
	 * 
	 * cascade="none": It's important here. The value of "none" here means upon
	 * inserting, saving, or deleting an Article, the associated Category is
	 * left untouched. A value of "delete" would mean "delete the associated
	 * Category when the Article is deleted", which is obviously not somethind
	 * we want to happen. A value of "save" would mean save the associated
	 * Category, too, but that's not what we need either, because we want the
	 * parent Category to not change when the Article gets edited.
	 */
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}

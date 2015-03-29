package com.datazuul.commons.cms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ralf
 * @hibernate.class table="CATEGORIES"
 */
public class Category implements Serializable {
	private long id = 0;

	private String name;

	private Category parent;

	private List subcategories = new ArrayList();

	private List articles = new ArrayList();

	private List images = new ArrayList();

	private List texts = new ArrayList();

	private List videos = new ArrayList();

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public Category(long id) {
		this.id = id;
	}

	public boolean isNew() {
		return (id < 1);
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

	/**
	 * @return the name
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The parent category of this category.
	 * 
	 * @hibernate.many-to-one cascade="none" column="PARENTID"
	 */
	public Category getParent() {
		return parent;
	}

	public void setParent(Category category) {
		this.parent = category;
	}

	/**
	 * @hibernate.bag table="CATEGORY" lazy="true" cascade="delete"
	 *                inverse="true"
	 * @hibernate.collection-one-to-many 
	 *                                   class="de.pixotec.webapps.kms.model.Category"
	 * @hibernate.collection-key column="PARENTID"
	 */
	public List getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List subcategories) {
		this.subcategories = subcategories;
	}

	/**
	 * @return all articles in this category
	 * @hibernate.list table="ARTICLES" lazy="true" cascade="all"
	 * @hibernate.collection-one-to-many 
	 *                                   class="de.pixotec.webapps.kms.model.Article"
	 * @hibernate.collection-key column="CATEGORY"
	 */
	/*
	 * @hibernate.list: tells Hibernate that it is a 1-n relation and that the
	 * other end of the relation is stored in the ARTICLES table.
	 * 
	 * lazy="true": the Articles of a Category are not loaded when the Category
	 * is loaded from the database; rather, Hibernate waits until getArticles()
	 * is called and then loads the associated Articles.
	 * 
	 * @hibernate.collection-one-to-many: Without this tag, Hibernate doesn't
	 * know objects of which type should be created when the List is loaded into
	 * memory from the database.
	 * 
	 * @hibernate.collection-key: the column parameter specifies the name of the
	 * foreign key column in the ARTICLES table that references the parent
	 * Category in the CATEGORIES table.
	 */
	public List getArticles() {
		return articles;
	}

	/**
	 * For any persistable property, there should also be a setter method
	 * specified.
	 * 
	 * @param articles
	 */
	public void setArticles(List articles) {
		this.articles = articles;
	}

	public List getImages() {
		return images;
	}

	public void setImages(List images) {
		this.images = images;
	}

	public List getTexts() {
		return texts;
	}

	public void setTexts(List texts) {
		this.texts = texts;
	}

	public List getVideos() {
		return videos;
	}

	public void setVideos(List videos) {
		this.videos = videos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "category=" + getId() + "/" + getName();
		if (getParent() != null) {
			result += "; parent=" + getParent().getId() + "/"
					+ getParent().getName();
		}
		if (getSubcategories() != null && !getSubcategories().isEmpty()) {
			List subcategories = getSubcategories();
			result += "; subcategories: ";
			for (Iterator iterator = subcategories.iterator(); iterator
					.hasNext();) {
				Category subcategory = (Category) iterator.next();
				result += subcategory.getId() + "/";
				result += subcategory.getName() + ", ";
			}
		}
		return result;
	}

	public void removeChild(Category category) {
		if (subcategories != null && !subcategories.isEmpty()) {
			List newSubcategories = new ArrayList<Category>();
			for (Iterator iterator = subcategories.iterator(); iterator
					.hasNext();) {
				Category subcategory = (Category) iterator.next();
				if (subcategory.getId() != category.getId()) {
					newSubcategories.add(subcategory);
				}
			}
			setSubcategories(newSubcategories);
		}
	}
}

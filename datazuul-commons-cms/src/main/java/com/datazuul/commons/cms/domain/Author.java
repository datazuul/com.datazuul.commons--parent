package com.datazuul.commons.cms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ralf
 * @hibernate.class table="AUTHORS"
 */
public class Author implements Serializable {
	private long id = 0;

	private String dayOfBirth = null;

	private String dayOfDeath = null;

	private String firstname = null;

	private String surname = null;

	private Image image = null;

	private Article article = null;

	private List articles = new ArrayList();

	private List images = new ArrayList();

	private List texts = new ArrayList();

	private List videos = new ArrayList();

	public Author() {

	}

	public Author(String firstname, String surname) {
		this.firstname = firstname;
		this.surname = surname;
	}

	public Author(String firstname, String surname, String dayOfBirth,
			String dayOfDeath) {
		this.dayOfBirth = dayOfBirth;
		this.dayOfDeath = dayOfDeath;
		this.firstname = firstname;
		this.surname = surname;
	}

	public Author(long id) {
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
	 * @return the firstname
	 * @hibernate.property column="FIRSTNAME" length="50"
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the surname
	 * @hibernate.property column="SURNAME" length="50"
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname
	 *            the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getDayOfDeath() {
		return dayOfDeath;
	}

	public void setDayOfDeath(String dayOfDeath) {
		this.dayOfDeath = dayOfDeath;
	}

	public String getDayOfBirth() {
		return dayOfBirth;
	}

	public void setDayOfBirth(String dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
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

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List getArticles() {
		return articles;
	}

	public void setArticles(List articles) {
		this.articles = articles;
	}
}

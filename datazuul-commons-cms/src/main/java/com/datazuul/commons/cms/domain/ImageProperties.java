package com.datazuul.commons.cms.domain;

import java.io.Serializable;

/**
 * @author ralf
 */
public class ImageProperties implements Serializable {
	private String id;

	private int height;

	private int width;

	private String filename;

	private byte[] bytes;

	public String getId() {
		return id;
	}

	public ImageProperties(String id) {
		this.id = id;
	}

	public ImageProperties(int width, int height, String filename) {
		this.filename = filename;
		this.height = height;
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}
}

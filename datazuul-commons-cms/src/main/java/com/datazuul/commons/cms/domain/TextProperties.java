package com.datazuul.commons.cms.domain;

import java.io.Serializable;

/**
 * @author ralf
 */
public class TextProperties implements Serializable {
	private String id;

	private String filename;

	private byte[] bytes;

	public String getId() {
		return id;
	}

	public TextProperties(String id) {
		this.id = id;
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

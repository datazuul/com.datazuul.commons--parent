package com.datazuul.commons.cms.backend;

import java.io.Serializable;

public class SearchResult implements Serializable {
	Long handle = null;
	String highlightedText = null;

	/**
	 * @return the handle
	 */
	public Long getHandle() {
		return handle;
	}

	/**
	 * @param handle
	 *            the handle to set
	 */
	public void setHandle(Long handle) {
		this.handle = handle;
	}

	/**
	 * @return the highlightedText
	 */
	public String getHighlightedText() {
		return highlightedText;
	}

	/**
	 * @param highlightedText
	 *            the highlightedText to set
	 */
	public void setHighlightedText(String highlightedText) {
		this.highlightedText = highlightedText;
	}
}

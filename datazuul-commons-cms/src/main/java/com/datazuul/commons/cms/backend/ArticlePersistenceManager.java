package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.DomainName;

public abstract class ArticlePersistenceManager {
	public void save(final DomainName dn, final Article article) {
		saveHTMLContent(dn, article);
		saveMetadata(dn, article);
	}

	/**
	 * Load article from storage.
	 * 
	 * @param article
	 *            contains id
	 * @return article null if error occurs or no valid id
	 */
	public Article load(DomainName dn, Article article) {
		Article result = loadMetadata(dn, article);
		if (result != null) {
			loadHTMLContent(dn, result);
		}
		return result;
	}

	protected abstract void loadHTMLContent(DomainName dn, Article article);

	protected abstract Article loadMetadata(DomainName dn, Article article);

	/**
	 * Save html content.
	 * 
	 * @param article
	 *            article to save
	 */
	protected abstract void saveHTMLContent(final DomainName dn,
			final Article article);

	/**
	 * Save meta data.
	 * 
	 * @param article
	 *            article to save
	 */
	protected abstract void saveMetadata(final DomainName dn,
			final Article article);
}

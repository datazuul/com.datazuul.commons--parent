package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;

public abstract class AuthorPersistenceManager {
    public void save(final DomainName dn, final Author author) {
	saveArticle(dn, author.getArticle());
	saveImage(dn, author.getImage());
	saveMetadata(dn, author);
    }

    /**
     * Load author from storage.
     * 
     * @param author
     *            contains id
     * @return author null if error occurs or no valid id
     */
    public Author load(final DomainName dn, final Author author) {
	return loadMetadata(dn, author);
    }

    /**
     * Save article about author.
     * 
     * @param article
     *            article to save
     */
    protected abstract void saveArticle(final DomainName dn, final Article article);

    /**
     * Save image of author.
     * 
     * @param image
     *            image to save
     */
    protected abstract void saveImage(final DomainName dn, final Image image);

    /**
     * Save meta data.
     * 
     * @param author
     *            to save
     */
    protected abstract void saveMetadata(final DomainName dn, final Author author);

    protected abstract Author loadMetadata(DomainName dn, Author author);
}

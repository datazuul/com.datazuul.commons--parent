package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.AuthorPersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLAuthor;
import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.framework.persistence.file.FileWriter;

public class FileAuthorPersistenceManager extends AuthorPersistenceManager {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.AuthorPersistenceManager#saveArticle(de
     * .pixotec.webapps.kms.model.Article)
     */
    @Override
    protected void saveArticle(final DomainName dn, final Article article) {
	FilePersistenceManager.getInstance(dn).save(dn, article);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.AuthorPersistenceManager#saveImage(de.
     * pixotec.webapps.kms.model.Image)
     */
    @Override
    protected void saveImage(final DomainName dn, final Image image) {
	FilePersistenceManager.getInstance(dn).save(dn, image);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.AuthorPersistenceManager#saveMetadata(
     * de.pixotec.webapps.kms.model.Author)
     */
    @Override
    protected void saveMetadata(final DomainName dn, final Author author) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, author);
	final File file = new File(filepath);
	final String content = new XMLAuthor(author).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.AuthorPersistenceManager#loadMetadata(
     * de.pixotec.webapps.kms.model.Author)
     */
    @Override
    protected Author loadMetadata(final DomainName dn, final Author author) {
	Author result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, author);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLAuthor xml = new XMLAuthor(metaDoc);

		author.setArticle(xml.getArticle());
		author.setArticles(xml.getArticles());
		author.setDayOfBirth(xml.getDayOfBirth());
		author.setDayOfDeath(xml.getDayOfDeath());
		author.setFirstname(xml.getFirstname());
		author.setImage(xml.getImage());
		author.setImages(xml.getImages());
		author.setSurname(xml.getSurname());
		author.setTexts(xml.getTexts());
		author.setVideos(xml.getVideos());

		result = author;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }
}
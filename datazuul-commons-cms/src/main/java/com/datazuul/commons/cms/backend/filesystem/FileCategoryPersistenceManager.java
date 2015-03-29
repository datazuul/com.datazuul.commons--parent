package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.CategoryPersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLCategory;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.framework.persistence.file.FileWriter;

public class FileCategoryPersistenceManager extends CategoryPersistenceManager {
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.CategoryPersistenceManager#saveMetadata
     * (de.pixotec.webapps.kms.model.Category)
     */
    @Override
    protected void saveMetadata(final DomainName dn, final Category category) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, category);
	final File file = new File(filepath);
	final String content = new XMLCategory(category).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    @Override
    protected Category loadMetadata(final DomainName dn, final Category category) {
	Category result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, category);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLCategory xml = new XMLCategory(metaDoc);

		category.setArticles(xml.getArticles());
		category.setImages(xml.getImages());
		category.setName(xml.getName());
		category.setParent(xml.getParent());
		category.setSubcategories(xml.getSubcategories());
		category.setTexts(xml.getTexts());
		category.setVideos(xml.getVideos());

		result = category;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }
}

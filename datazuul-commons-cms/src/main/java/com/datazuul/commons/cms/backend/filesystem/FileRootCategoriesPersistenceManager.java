package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.RootCategoriesPersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLRoot;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.framework.persistence.file.FileWriter;

public class FileRootCategoriesPersistenceManager extends RootCategoriesPersistenceManager {
    @Override
    public List<Category> load(final DomainName dn) {
	final List<Category> result = new ArrayList<Category>();
	final String filepath = FilePathFactory.getInstance().getRootFilePath(dn, Category.class);

	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLRoot xml = new XMLRoot(metaDoc);

		final List<Long> children = xml.getChildren();
		for (final Iterator iterator = children.iterator(); iterator.hasNext();) {
		    final Long id = (Long) iterator.next();
		    final Category child = new Category(id.longValue());
		    result.add(child);
		}
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }

    @Override
    public void save(final DomainName dn, final List<Category> rootCategories) {
	final String filepath = FilePathFactory.getInstance().getRootFilePath(dn, Category.class);
	final File file = new File(filepath);
	final List<Long> children = new ArrayList<Long>();
	for (final Iterator iterator = rootCategories.iterator(); iterator.hasNext();) {
	    final Category category = (Category) iterator.next();
	    final Long id = new Long(category.getId());
	    children.add(id);
	}
	final String content = new XMLRoot(children).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);

    }
}

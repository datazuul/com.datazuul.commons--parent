package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.TextPersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLText;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.framework.persistence.file.FileWriter;

/**
 * @author ralf
 */
public class FileTextPersistenceManager extends TextPersistenceManager {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.TextPersistenceManager#saveBinary(de.pixotec
     * .webapps.kms.model.Text)
     */
    @Override
    protected void saveBinary(final DomainName dn, final Text text) {
	// {id}.{mimetype}
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, text);
	final File file = new File(filepath);
	FileWriter.writeContent(file, text.getProperties().getBytes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.TextPersistenceManager#saveMetadata(de
     * .pixotec.webapps.kms.model.Text)
     */
    @Override
    protected void saveMetadata(final DomainName dn, final Text text) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, text);
	final File file = new File(filepath);
	final String content = new XMLText(text).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    @Override
    protected void loadBinary(final DomainName dn, final Text text) {
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, text);
	final File file = new File(filepath);
	FileImageInputStream fiis;
	try {
	    fiis = new FileImageInputStream(file);
	    final byte[] bytes = new byte[(int) fiis.length()];
	    fiis.read(bytes);
	    text.getProperties().setBytes(bytes);
	    fiis.close();
	} catch (final FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    protected Text loadMetadata(final DomainName dn, final Text text) {
	Text result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, text);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLText xml = new XMLText(metaDoc);

		text.setAuthor(xml.getAuthor());
		text.setCategories(xml.getCategories());
		text.setDescription(xml.getDescription());
		text.setFormat(xml.getFormat());
		text.setTitle(xml.getTitle());

		result = text;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }
}

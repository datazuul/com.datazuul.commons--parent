package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.VideoPersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLVideo;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Video;
import com.datazuul.framework.persistence.file.FileWriter;

/**
 * @author ralf
 */
public class FileVideoPersistenceManager extends VideoPersistenceManager {
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.TextPersistenceManager#saveBinary(de.pixotec
     * .webapps.kms.model.Text)
     */
    @Override
    protected void saveBinary(final DomainName dn, final Video video) {
	// {id}.{mimetype}
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, video);
	final File file = new File(filepath);
	FileWriter.writeContent(file, video.getProperties().getBytes());
    }

    @Override
    protected void saveMetadata(final DomainName dn, final Video video) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, video);
	final File file = new File(filepath);
	final String content = new XMLVideo(video).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    @Override
    protected void loadBinary(final DomainName dn, final Video video) {
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, video);
	final File file = new File(filepath);
	FileImageInputStream fiis;
	try {
	    fiis = new FileImageInputStream(file);
	    final byte[] bytes = new byte[(int) fiis.length()];
	    fiis.read(bytes);
	    video.getProperties().setBytes(bytes);
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
    protected Video loadMetadata(final DomainName dn, final Video video) {
	Video result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, video);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLVideo xml = new XMLVideo(metaDoc);

		video.setAuthor(xml.getAuthor());
		video.setCategories(xml.getCategories());
		video.setDescription(xml.getDescription());
		video.setFormat(xml.getFormat());
		video.setTitle(xml.getTitle());

		result = video;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }
}

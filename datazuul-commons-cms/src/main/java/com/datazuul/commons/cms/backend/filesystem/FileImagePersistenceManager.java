package com.datazuul.commons.cms.backend.filesystem;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.ImagePersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLImage;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.ImageProperties;
import com.datazuul.framework.persistence.file.FileWriter;

public class FileImagePersistenceManager extends ImagePersistenceManager {
    private BufferedImage bi = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.pixotec.webapps.kms.backend.ImagePersistenceManager#saveMetadata(de
     * .pixotec.webapps.kms.model.Image)
     */
    @Override
    protected void saveMetadata(final DomainName dn, final Image image) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, image);
	final File file = new File(filepath);
	final String content = new XMLImage(image).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    @Override
    protected void saveOriginal(final DomainName dn, final Image image) {
	// {id}.{mimetype}
	// save with original size
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, image);
	final File file = new File(filepath);
	FileWriter.writeContent(file, image.getPropsOriginal().getBytes());

	// read original as BufferedImage
	try {
	    bi = ImageIO.read(file);
	    final ImageProperties propsOriginal = image.getPropsOriginal();
	    propsOriginal.setFilename(file.getName());
	    propsOriginal.setHeight(bi.getHeight());
	    propsOriginal.setWidth(bi.getWidth());
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    protected void savePreview(final DomainName dn, final Image image) {
	// save preview (middle size)
	// TODO make width and height configurable
	final String filepath = FilePathFactory.getInstance().getPreviewFilePath(dn, image);
	final File file = new File(filepath);
	try {
	    final ImageProperties newPropsPreview = saveImageScaled(bi, 250, 250, image.getFormat(), file);
	    final ImageProperties propsPreview = image.getPropsPreview();
	    propsPreview.setFilename(file.getName());
	    propsPreview.setHeight(newPropsPreview.getHeight());
	    propsPreview.setWidth(newPropsPreview.getWidth());
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    protected void saveThumbnail(final DomainName dn, final Image image) {
	// save thumbnail (small size)
	// TODO make width and height configurable
	final String filepath = FilePathFactory.getInstance().getThumbnailFilePath(dn, image);
	final File file = new File(filepath);
	try {
	    final ImageProperties newPropsThumbnail = saveImageScaled(bi, 100, 100, image.getFormat(), file);
	    final ImageProperties propsThumbnail = image.getPropsThumbnail();
	    propsThumbnail.setFilename(file.getName());
	    propsThumbnail.setHeight(newPropsThumbnail.getHeight());
	    propsThumbnail.setWidth(newPropsThumbnail.getWidth());
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private ImageProperties saveImageScaled(final BufferedImage image, final int maxWidth, final int maxHeight,
	    final String format, final File file) throws IOException {
	int targetWidth = maxWidth;
	int targetHeight = maxHeight;
	final double targetRatio = (double) maxWidth / (double) maxHeight;

	final int imageWidth = image.getWidth(null);
	final int imageHeight = image.getHeight(null);
	final double srcRatio = (double) imageWidth / (double) imageHeight;
	if (targetRatio < srcRatio) {
	    targetHeight = (int) (targetWidth / srcRatio);
	} else {
	    targetWidth = (int) (targetHeight * srcRatio);
	}

	final java.awt.Image scaledImage = image.getScaledInstance(targetWidth, targetHeight,
		java.awt.Image.SCALE_DEFAULT);

	saveImage(scaledImage, format, file);

	return new ImageProperties(targetWidth, targetHeight, file.getName());
    }

    private void saveImage(final java.awt.Image image, final String format, final File file) throws IOException {
	final int w = image.getWidth(null);
	final int h = image.getHeight(null);
	final BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	final Graphics2D g2 = bi.createGraphics();
	g2.drawImage(image, 0, 0, null);
	g2.dispose();
	ImageIO.write(bi, format, file);
    }

    @Override
    protected Image loadMetadata(final DomainName dn, final Image image) {
	Image result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, image);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLImage xml = new XMLImage(metaDoc);

		image.setAlign(xml.getAlign());
		image.setAuthor(xml.getAuthor());
		image.setCategories(xml.getCategories());
		image.setDescription(xml.getDescription());
		image.setFormat(xml.getFormat());
		image.setPropsOriginal(xml.getPropsOriginal());
		image.setPropsPreview(xml.getPropsPreview());
		image.setPropsThumbnail(xml.getPropsThumbnail());
		image.setTitle(xml.getTitle());
		image.setValign(xml.getValign());

		result = image;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }

    @Override
    protected void loadOriginal(final DomainName dn, final Image image) {
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, image);
	final File file = new File(filepath);
	FileImageInputStream fiis;
	try {
	    fiis = new FileImageInputStream(file);
	    final byte[] bytes = new byte[(int) fiis.length()];
	    fiis.read(bytes);
	    image.getPropsOriginal().setBytes(bytes);
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
    protected void loadPreview(final DomainName dn, final Image image) {
	final String filepath = FilePathFactory.getInstance().getPreviewFilePath(dn, image);
	final File file = new File(filepath);
	try {
	    final FileImageInputStream fiis = new FileImageInputStream(file);
	    final byte[] bytes = new byte[(int) fiis.length()];
	    fiis.read(bytes);
	    image.getPropsPreview().setBytes(bytes);
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
    protected void loadThumbnail(final DomainName dn, final Image image) {
	final String filepath = FilePathFactory.getInstance().getThumbnailFilePath(dn, image);
	final File file = new File(filepath);
	FileImageInputStream fiis;
	try {
	    fiis = new FileImageInputStream(file);
	    final byte[] bytes = new byte[(int) fiis.length()];
	    fiis.read(bytes);
	    image.getPropsThumbnail().setBytes(bytes);
	    fiis.close();
	} catch (final FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
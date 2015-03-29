package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.util.Properties;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.commons.cms.domain.Video;
import com.datazuul.framework.util.PropertiesLoader;

/**
 * @author ralf
 */
public class FilePathFactory {
    private static FilePathFactory instance;

    private static final String FILEEXTENSION_ARTICLES = ".html";

    private static final String FILEEXTENSION_METAFILES = ".xml";

    private static Properties PROPS = null;

    private static String REPOSITORY_ROOT = null;

    private static final String FILENAME_ROOT = "root";

    private static final String FILENAME_SITE = "site";

    private static final String SUBDIR_ARTICLES = "articles";

    private static final String SUBDIR_AUTHORS = "authors";

    private static final String SUBDIR_CATEGORIES = "categories";

    private static final String SUBDIR_IMAGES = "images";

    private static final String SUBDIR_TEXTS = "texts";

    private static final String SUBDIR_VIDEOS = "videos";

    private FilePathFactory() {
	try {
	    // Load the FilePathFactory.properties file
	    PROPS = PropertiesLoader.loadParams("FilePathFactory");
	    REPOSITORY_ROOT = (String) PROPS.get("RepositoryRoot");
	    if (!REPOSITORY_ROOT.endsWith(File.separator)) {
		REPOSITORY_ROOT += File.separator;
	    }
	} catch (final Exception ex) {
	    System.out.println(" Fatal Error : Couldn't Read FilePathFactory.properties file: " + ex.toString());
	}
    }

    public static FilePathFactory getInstance() {
	if (instance == null) {
	    instance = new FilePathFactory();
	}
	return instance;
    }

    public String getMetaFilePath(final DomainName dn, final Article article) {
	final StringBuffer sb = new StringBuffer(getRootPathArticles(dn));
	sb.append(convertId2Path(article.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getMetaFilePath(final DomainName dn, final Author author) {
	final StringBuffer sb = new StringBuffer(getRootPathAuthors(dn));
	sb.append(convertId2Path(author.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getMetaFilePath(final DomainName dn, final Category category) {
	final StringBuffer sb = new StringBuffer(getRootPathCategories(dn));
	sb.append(convertId2Path(category.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    /**
     * Provides a simple path logic.<br>
     * Each <code>Image</code> is represented by a file, filename
     * "{imageId}.(jpg|jpeg|png|gif)", and the corresponding XML-metadata-file.
     * An Image is stored in a specific directory-path under the root dir of the
     * repository: the image specific directory-path follows the following rule:
     * 
     * subdirectory-path is the imageId splitted in 2 digits subdirs (starting
     * from imageId head), e.g.:<br>
     * 
     * <pre>
     * imageId =  1, path = repositoryroot/images/1.xml
     * imageId =  9, path = repositoryroot/images/9.xml
     * imageId = 10, path = repositoryroot/images/10/10.xml
     * imageId = 11, path = repositoryroot/images/11/11.xml
     * imageId = 101, path = repositoryroot/images/10/101.xml
     * imageId = 110, path = repositoryroot/images/11/110.xml
     * imageId = 1000, path = repositoryroot/images/10/00/1000.xml
     * imageId = 1001, path = repositoryroot/images/10/01/1001.xml
     * imageId = 13425, path = repositoryroot/images/13/42/13425.xml
     * imageId = 13426795, path = repositoryroot/images/13/42/67/95/13426795.xml
     * </pre>
     */
    public String getMetaFilePath(final DomainName dn, final Image image) {
	final StringBuffer sb = new StringBuffer(getRootPathImages(dn));
	sb.append(convertId2Path(image.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getMetaFilePath(final DomainName dn, final Text text) {
	final StringBuffer sb = new StringBuffer(getRootPathTexts(dn));
	sb.append(convertId2Path(text.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getMetaFilePath(final DomainName dn, final Video video) {
	final StringBuffer sb = new StringBuffer(getRootPathVideos(dn));
	sb.append(convertId2Path(video.getId()));
	sb.append(FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getRootFilePath(final DomainName dn, final Class cls) {
	final StringBuffer sb = new StringBuffer();
	if (cls == Category.class) {
	    sb.append(getRootPathCategories(dn));
	}
	sb.append(FILENAME_ROOT + FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    public String getSiteFilePath(final DomainName dn, final Class cls) {
	final StringBuffer sb = new StringBuffer();
	if (cls == Category.class) {
	    sb.append(getRootPathCategories(dn));
	}
	sb.append(FILENAME_SITE + FILEEXTENSION_METAFILES);
	return sb.toString();
    }

    private String convertId2Path(final long m) {
	final String idStr = String.valueOf(m);
	if (idStr.length() == 1) {
	    return "0" + idStr;
	}
	String result = "";
	final int l = idStr.length();
	final int steps = Math.round(l / 2);
	for (int i = 0; i < steps; i++) {
	    result += idStr.substring((i * 2), (i * 2) + 2) + File.separator;
	}
	result += idStr;
	return result;
    }

    public String getRootPathArticles(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_ARTICLES).append(File.separator);
	return sb.toString();
    }

    public String getRootPathAuthors(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_AUTHORS).append(File.separator);
	return sb.toString();
    }

    public String getRootPathCategories(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_CATEGORIES).append(File.separator);
	return sb.toString();
    }

    public String getRootPathImages(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_IMAGES).append(File.separator);
	return sb.toString();
    }

    public String getRootPathTexts(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_TEXTS).append(File.separator);
	return sb.toString();
    }

    public String getRootPathVideos(final DomainName dn) {
	final StringBuffer sb = new StringBuffer(getRootPathRepository(dn));
	sb.append(SUBDIR_VIDEOS).append(File.separator);
	return sb.toString();
    }

    /**
     * @return the REPOSITORY_ROOT (with replaced dynamic placeholders)
     */
    public static String getRootPathRepository(final DomainName dn) {
	String rootPathRepository = new String(REPOSITORY_ROOT);
	rootPathRepository = rootPathRepository.replaceAll("\\$\\{domain\\}", dn.getFullyQualifiedDomainName());
	return rootPathRepository;
    }

    /**
     * Provides a simple path logic.<br>
     * Each <code>Article</code> is represented by a file, filename
     * "{articleId}.html". An Article is stored in a specific directory-path
     * under the root dir of the repository: the article specific directory-path
     * follows the following rule:
     * 
     * subdirectory-path is the articleId splitted in 2 digits subdirs (starting
     * from articleId head), e.g.: articleId = 1, path =
     * repositoryroot/articles/1.xml articleId = 9, path =
     * repositoryroot/articles/9.xml articleId = 10, path =
     * repositoryroot/articles/10/10.xml articleId = 11, path =
     * repositoryroot/articles/11/11.xml articleId = 101, path =
     * repositoryroot/articles/10/101.xml articleId = 110, path =
     * repositoryroot/articles/11/110.xml articleId = 1000, path =
     * repositoryroot/articles/10/00/1000.xml articleId = 1001, path =
     * repositoryroot/articles/10/01/1001.xml articleId = 13425, path =
     * repositoryroot/articles/13/42/13425.xml articleId = 13426795, path =
     * repositoryroot/articles/13/42/67/95/13426795.xml
     */
    public String getFilePath(final DomainName dn, final Article article) {
	final StringBuffer sb = new StringBuffer(getRootPathArticles(dn));
	sb.append(convertId2Path(article.getId()));
	sb.append(FILEEXTENSION_ARTICLES);
	return sb.toString();
    }

    public String getPreviewFilePath(final DomainName dn, final Image img) {
	final StringBuffer sb = new StringBuffer(getRootPathImages(dn));
	sb.append(convertId2Path(img.getId()));
	sb.append("_preview.").append(img.getFormat().toLowerCase());
	return sb.toString();
    }

    public String getThumbnailFilePath(final DomainName dn, final Image img) {
	final StringBuffer sb = new StringBuffer(getRootPathImages(dn));
	sb.append(convertId2Path(img.getId()));
	if (img.getFormat() == null) {
	    return null;
	}
	sb.append("_thumbnail.").append(img.getFormat().toLowerCase());
	return sb.toString();
    }

    public String getFilePath(final DomainName dn, final Author author) {
	final StringBuffer sb = new StringBuffer(getRootPathAuthors(dn));
	sb.append(convertId2Path(author.getId()));
	sb.append(FILEEXTENSION_ARTICLES);
	return sb.toString();
    }

    public String getFilePath(final DomainName dn, final Image img) {
	final StringBuffer sb = new StringBuffer(getRootPathImages(dn));
	sb.append(convertId2Path(img.getId()));
	sb.append(".").append(img.getFormat().toLowerCase());
	return sb.toString();
    }

    public String getFilePath(final DomainName dn, final Text text) {
	final StringBuffer sb = new StringBuffer(getRootPathTexts(dn));
	sb.append(convertId2Path(text.getId()));
	sb.append(".").append(text.getFormat().toLowerCase());
	return sb.toString();
    }

    public String getFilePath(final DomainName dn, final Video video) {
	final StringBuffer sb = new StringBuffer(getRootPathVideos(dn));
	sb.append(convertId2Path(video.getId()));
	sb.append(".").append(video.getFormat().toLowerCase());
	return sb.toString();
    }
}

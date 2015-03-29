package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.datazuul.commons.cms.backend.ArticlePersistenceManager;
import com.datazuul.commons.cms.backend.filesystem.xml.XMLArticle;
import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.framework.persistence.file.FileWriter;

/**
 * Provides a simple persistence logic.<br>
 * Each <code>Article</code> is represented by two files:
 * <ul>
 * <li>articleId}.html for the html-content</li>
 * <li>articleId}.xml for the article's metadata (title, author, revisions, ...)
 * </li>
 * </ul>
 * . An Article is stored in a specific directory-path under the root dir of the
 * repository: the article specific directory-path follows the following rule:
 * 
 * subdirectory-path is the articleId splitted in 2 digits subdirs (starting
 * from articleId head), e.g.:
 * 
 * <pre>
 *         articleId = 1, path = repositoryroot/articles/1.xml and 1.html
 *         articleId = 9, path = repositoryroot/articles/9.xml and 9.html
 *         articleId = 10, path = repositoryroot/articles/10/10.xml and 10.html
 *         articleId = 11, path = repositoryroot/articles/11/11.xml and 11.html
 *         articleId = 101, path = repositoryroot/articles/10/101.xml and 101.html
 *         articleId = 110, path = repositoryroot/articles/11/110.xml and 110.html
 *         articleId = 1000, path = repositoryroot/articles/10/00/1000.xml and 1000.html
 *         articleId = 1001, path = repositoryroot/articles/10/01/1001.xml and 1001.html
 *         articleId = 13425, path = repositoryroot/articles/13/42/13425.xml and 12345.html
 *         articleId = 13426795, path = repositoryroot/articles/13/42/67/95/13426795.xml and 13426795.html
 * </pre>
 * 
 * <p>
 * The format of the meta-data-file (xml) is this:
 * 
 * <pre>
 *         &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
 *         
 *         &lt;article&gt;
 *           &lt;artheader&gt;
 *             &lt;author&gt;
 *               &lt;firstname&gt;Ralf&lt;/firstname&gt;
 *               &lt;surname&gt;Eichinger&lt;/surname&gt;
 *             &lt;/author&gt;
 *             &lt;authorinitials&gt;RWE&lt;/authorinitials&gt;
 *             &lt;title&gt;Glauben, Religionen, Götter&lt;/title&gt;
 *             &lt;revhistory&gt;
 *               &lt;revision&gt;
 *                 &lt;revnumber&gt;1.0&lt;/revnumber&gt;
 *                 &lt;date&gt;13.07.2001&lt;/date&gt;
 *                 &lt;revremark&gt;erstellt&lt;/revremark&gt;
 *               &lt;/revision&gt;
 *             &lt;/revhistory&gt;
 *           &lt;/artheader&gt;
 *         &lt;/article&gt;
 * </pre>
 */
public class FileArticlePersistenceManager extends ArticlePersistenceManager {
    /**
     * Save html content into file {id}.html.
     * 
     * @see com.datazuul.commons.cms.backend.pixotec.webapps.kms.backend.ArticlePersistenceManager#saveHTMLContent(de.pixotec.webapps.kms.model.Article)
     */
    @Override
    protected void saveHTMLContent(final DomainName dn, final Article article) {
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, article);
	final File file = new File(filepath);
	String content = article.getHtmlContent();
	if (content == null) {
	    content = "";
	}
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);

    }

    /**
     * Save meta data into file {id}.xml
     * 
     * @see com.datazuul.commons.cms.backend.pixotec.webapps.kms.backend.ArticlePersistenceManager#saveMetadata(de.pixotec.webapps.kms.model.Article)
     */
    @Override
    protected void saveMetadata(final DomainName dn, final Article article) {
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, article);
	final File file = new File(filepath);
	final String content = new XMLArticle(article).toString();
	FileWriter.writeContent(file, content, FilePersistenceManager.DEFAULT_ENCODING);
    }

    @Override
    protected void loadHTMLContent(final DomainName dn, Article article) {
	// read {id}.html
	final String filepath = FilePathFactory.getInstance().getFilePath(dn, article);
	final File file = new File(filepath);
	if (!file.exists()) {
	    article = null;
	} else {
	    final StringBuffer sb = new StringBuffer();
	    try {
		final FileInputStream fis = new FileInputStream(file);
		final InputStreamReader isr = new InputStreamReader(fis, FilePersistenceManager.DEFAULT_ENCODING);
		while (isr.ready()) {
		    final Character c = new Character((char) isr.read());
		    sb.append(c);
		}
		article.setHtmlContent(sb.toString());
	    } catch (final FileNotFoundException e) {
		article = null;
	    } catch (final UnsupportedEncodingException e) {
		article = null;
	    } catch (final IOException e) {
		article = null;
	    }
	}
    }

    @Override
    protected Article loadMetadata(final DomainName dn, final Article article) {
	Article result = null;
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, article);
	final File file = new File(filepath);
	if (file.exists()) {
	    final SAXBuilder builder = new SAXBuilder();
	    try {
		final Document metaDoc = builder.build(file);
		final XMLArticle xml = new XMLArticle(metaDoc);

		article.setAuthor(xml.getAuthor());
		article.setCategory(xml.getCategory());
		article.setTitle(xml.getTitle());

		result = article;
	    } catch (final JDOMException e) {
	    } catch (final IOException e) {
	    }
	}
	return result;
    }
}

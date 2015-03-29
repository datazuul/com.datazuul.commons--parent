package com.datazuul.commons.cms.backend.filesystem.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.commons.cms.domain.Video;

/**
 * Meta data of an Author-object in XML-format. TODO: sorting...
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
 * 
 * &lt;author article=&quot;123&quot; image=&quot;12&quot;&gt;
 *   &lt;firstname&gt;Ralf&lt;/firstname&gt;
 *   &lt;surname&gt;Eichinger&lt;/surname&gt;
 *   &lt;dayofbirth&gt;08.12.1970&lt;/dayofbirth&gt;
 *   &lt;dayofdeath&gt;&lt;/dayofdeath&gt;
 *   &lt;articles&gt;
 *     &lt;article id=&quot;17&quot; /&gt;
 *     &lt;article id=&quot;101&quot; /&gt;
 *   &lt;/articles&gt;
 *   &lt;images&gt;
 *     &lt;image id=&quot;17&quot; /&gt;
 *     &lt;image id=&quot;101&quot; /&gt;
 *   &lt;/images&gt;
 *   &lt;texts /&gt;
 *   &lt;videos /&gt;
 * &lt;/author&gt; *
 * </pre>
 * 
 * @author ralf
 */
public class XMLAuthor extends XMLObject {
    private final Element author;

    public XMLAuthor(final Document pMetaDoc) {
	document = pMetaDoc;
	author = document.getRootElement();
    }

    public XMLAuthor(final Author pAuthor) {
	// <author> (root element)
	author = new Element("author");
	// create document
	document = new Document(author);

	if (pAuthor.getArticle() != null) {
	    author.setAttribute("article", String.valueOf(pAuthor.getArticle().getId()));
	}

	if (pAuthor.getImage() != null) {
	    author.setAttribute("image", String.valueOf(pAuthor.getImage().getId()));
	}

	// <firstname>
	if (pAuthor.getFirstname() != null) {
	    final Element e = new Element("firstname");
	    e.setText(pAuthor.getFirstname());
	    author.addContent(e);
	}

	// <surname>
	if (pAuthor.getSurname() != null) {
	    final Element e = new Element("surname");
	    e.setText(pAuthor.getSurname());
	    author.addContent(e);
	}

	// <dayofbirth>
	if (pAuthor.getDayOfBirth() != null) {
	    final Element e = new Element("dayofbirth");
	    e.setText(pAuthor.getDayOfBirth());
	    author.addContent(e);
	}

	// <dayofdeath>
	if (pAuthor.getDayOfDeath() != null) {
	    final Element e = new Element("dayofdeath");
	    e.setText(pAuthor.getDayOfDeath());
	    author.addContent(e);
	}

	// <articles>
	Element e = new Element("articles");
	author.addContent(e);
	// <article>
	if (!pAuthor.getArticles().isEmpty()) {
	    for (final Iterator iter = pAuthor.getArticles().iterator(); iter.hasNext();) {
		final Article pArticle = (Article) iter.next();
		final Element el = new Element("article");
		el.setAttribute("id", String.valueOf(pArticle.getId()));
		e.addContent(el);
	    }
	}

	// <images>
	e = new Element("images");
	author.addContent(e);
	// <image>
	if (!pAuthor.getImages().isEmpty()) {
	    for (final Iterator iter = pAuthor.getImages().iterator(); iter.hasNext();) {
		final Image pImage = (Image) iter.next();
		final Element el = new Element("image");
		el.setAttribute("id", String.valueOf(pImage.getId()));
		e.addContent(el);
	    }
	}

	// <texts>
	e = new Element("texts");
	author.addContent(e);
	// <text>
	if (!pAuthor.getTexts().isEmpty()) {
	    for (final Iterator iter = pAuthor.getTexts().iterator(); iter.hasNext();) {
		final Text pText = (Text) iter.next();
		final Element el = new Element("text");
		el.setAttribute("id", String.valueOf(pText.getId()));
		e.addContent(el);
	    }
	}

	// <videos>
	e = new Element("videos");
	author.addContent(e);
	// <video>
	if (!pAuthor.getVideos().isEmpty()) {
	    for (final Iterator iter = pAuthor.getVideos().iterator(); iter.hasNext();) {
		final Video pVideo = (Video) iter.next();
		final Element el = new Element("video");
		el.setAttribute("id", String.valueOf(pVideo.getId()));
		e.addContent(el);
	    }
	}
    }

    public Article getArticle() {
	Article result = null;
	final String id = author.getAttributeValue("article");
	if (id != null) {
	    result = new Article(Long.parseLong(id));
	}
	return result;
    }

    public Image getImage() {
	Image result = null;
	final String id = author.getAttributeValue("image");
	if (id != null) {
	    result = new Image(Long.parseLong(id));
	}
	return result;
    }

    public String getDayOfBirth() {
	return author.getChildText("dayofbirth");
    }

    public String getDayOfDeath() {
	return author.getChildText("dayofdeath");
    }

    public String getFirstname() {
	return author.getChildText("firstname");
    }

    public String getSurname() {
	return author.getChildText("surname");
    }

    public List getArticles() {
	final ArrayList result = new ArrayList();
	final Element articles = author.getChild("articles");
	if (articles != null && articles.getChildren() != null) {
	    final List articleList = articles.getChildren();
	    for (final Iterator iter = articleList.iterator(); iter.hasNext();) {
		final Element article = (Element) iter.next();
		final long id = Long.parseLong(article.getAttributeValue("id"));
		result.add(new Article(id));
	    }
	}
	return result;
    }

    public List getImages() {
	final ArrayList result = new ArrayList();
	final Element images = author.getChild("images");
	if (images != null && images.getChildren() != null) {
	    final List imageList = images.getChildren();
	    for (final Iterator iter = imageList.iterator(); iter.hasNext();) {
		final Element image = (Element) iter.next();
		final long id = Long.parseLong(image.getAttributeValue("id"));
		result.add(new Image(id));
	    }
	}
	return result;
    }

    public List getTexts() {
	final ArrayList result = new ArrayList();
	final Element texts = author.getChild("texts");
	if (texts != null && texts.getChildren() != null) {
	    final List textList = texts.getChildren();
	    for (final Iterator iter = textList.iterator(); iter.hasNext();) {
		final Element text = (Element) iter.next();
		final long id = Long.parseLong(text.getAttributeValue("id"));
		result.add(new Text(id));
	    }
	}
	return result;
    }

    public List getVideos() {
	final ArrayList result = new ArrayList();
	final Element videos = author.getChild("videos");
	if (videos != null && videos.getChildren() != null) {
	    final List videoList = videos.getChildren();
	    for (final Iterator iter = videoList.iterator(); iter.hasNext();) {
		final Element video = (Element) iter.next();
		final long id = Long.parseLong(video.getAttributeValue("id"));
		result.add(new Video(id));
	    }
	}
	return result;
    }
}

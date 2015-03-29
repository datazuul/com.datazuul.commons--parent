package com.datazuul.commons.cms.backend.filesystem.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.commons.cms.domain.Video;

/**
 * TODO: sorting...
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
 * 
 * &lt;category name=&quot;Glauben, Religionen, Götter&quot; parent=&quot;1&quot;&gt;
 *   &lt;children sortedby=&quot;date&quot; sortdirection=&quot;asc&quot;&gt;
 *     &lt;category id=&quot;5&quot; /&gt;
 *     &lt;category id=&quot;7&quot; /&gt;
 *     &lt;category id=&quot;11&quot; /&gt;
 *   &lt;/children&gt;
 *   &lt;articles sortedby=&quot;author&quot;&gt;
 *     &lt;article id=&quot;4711&quot; /&gt;
 *   &lt;/articles&gt;
 *   &lt;images sortedby=&quot;title&quot; sortdirection=&quot;desc&quot;&gt;
 *     &lt;image id=&quot;3&quot; /&gt;
 *     &lt;image id=&quot;123&quot; /&gt;
 *   &lt;/images&gt;
 *   &lt;texts sortedby=&quot;title&quot; sortdirection=&quot;desc&quot;&gt;
 *     &lt;text id=&quot;3&quot; /&gt;
 *     &lt;text id=&quot;123&quot; /&gt;
 *   &lt;/texts&gt;
 *   &lt;videos sortedby=&quot;title&quot; sortdirection=&quot;desc&quot;&gt;
 *     &lt;video id=&quot;3&quot; /&gt;
 *     &lt;video id=&quot;123&quot; /&gt;
 *   &lt;/videos&gt;
 * &lt;/category&gt;
 * </pre>
 * 
 * @author ralf
 */
public class XMLCategory extends XMLObject {
    Element category = null;

    public XMLCategory(final Document pMetaDoc) {
	document = pMetaDoc;
	category = document.getRootElement();
    }

    public XMLCategory(final Category pCategory) {
	// <category> (root element)
	category = new Element("category");
	// create document
	document = new Document(category);

	if (pCategory.getName() != null) {
	    category.setAttribute("name", pCategory.getName());
	}
	if (pCategory.getParent() != null) {
	    category.setAttribute("parent", String.valueOf(pCategory.getParent().getId()));
	}

	// <children>
	Element e = new Element("children");
	category.addContent(e);
	// <category>
	if (!pCategory.getSubcategories().isEmpty()) {
	    for (final Iterator iter = pCategory.getSubcategories().iterator(); iter.hasNext();) {
		final Category subcategory = (Category) iter.next();
		final Element el = new Element("category");
		el.setAttribute("id", String.valueOf(subcategory.getId()));
		e.addContent(el);
	    }
	}

	// <articles>
	e = new Element("articles");
	category.addContent(e);
	// <article>
	if (!pCategory.getArticles().isEmpty()) {
	    for (final Iterator iter = pCategory.getArticles().iterator(); iter.hasNext();) {
		final Article article = (Article) iter.next();
		final Element el = new Element("article");
		el.setAttribute("id", String.valueOf(article.getId()));
		e.addContent(el);
	    }
	}

	// <images>
	e = new Element("images");
	category.addContent(e);
	// <image>
	if (!pCategory.getImages().isEmpty()) {
	    for (final Iterator iter = pCategory.getImages().iterator(); iter.hasNext();) {
		final Image image = (Image) iter.next();
		final Element el = new Element("image");
		el.setAttribute("id", String.valueOf(image.getId()));
		e.addContent(el);
	    }
	}

	// <texts>
	e = new Element("texts");
	category.addContent(e);
	// <text>
	if (!pCategory.getTexts().isEmpty()) {
	    for (final Iterator iter = pCategory.getTexts().iterator(); iter.hasNext();) {
		final Text pText = (Text) iter.next();
		final Element el = new Element("text");
		el.setAttribute("id", String.valueOf(pText.getId()));
		e.addContent(el);
	    }
	}

	// <videos>
	e = new Element("videos");
	category.addContent(e);
	// <video>
	if (!pCategory.getVideos().isEmpty()) {
	    for (final Iterator iter = pCategory.getVideos().iterator(); iter.hasNext();) {
		final Video pVideo = (Video) iter.next();
		final Element el = new Element("video");
		el.setAttribute("id", String.valueOf(pVideo.getId()));
		e.addContent(el);
	    }
	}
    }

    public String getName() {
	return category.getAttributeValue("name");
    }

    public Category getParent() {
	Category result = null;
	final String id = category.getAttributeValue("parent");
	if (id != null) {
	    result = new Category(Long.parseLong(id));
	}
	return result;
    }

    public List getArticles() {
	final ArrayList result = new ArrayList();
	final Element articles = category.getChild("articles");
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
	final Element images = category.getChild("images");
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

    public List getSubcategories() {
	final ArrayList result = new ArrayList();
	final Element subcategories = category.getChild("children");
	if (subcategories != null && subcategories.getChildren() != null) {
	    final List subcategoryList = subcategories.getChildren();
	    for (final Iterator iter = subcategoryList.iterator(); iter.hasNext();) {
		final Element category = (Element) iter.next();
		final long id = Long.parseLong(category.getAttributeValue("id"));
		result.add(new Category(id));
	    }
	}
	return result;
    }

    public List getTexts() {
	final ArrayList result = new ArrayList();
	final Element texts = category.getChild("texts");
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
	final Element videos = category.getChild("videos");
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

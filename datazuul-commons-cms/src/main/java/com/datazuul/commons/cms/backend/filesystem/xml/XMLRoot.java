package com.datazuul.commons.cms.backend.filesystem.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

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
 * &lt;root&gt;
 *   &lt;children sortedby=&quot;date&quot; sortdirection=&quot;asc&quot;&gt;
 *     &lt;child id=&quot;5&quot; /&gt;
 *     &lt;child id=&quot;7&quot; /&gt;
 *     &lt;child id=&quot;11&quot; /&gt;
 *   &lt;/children&gt;
 * &lt;/root&gt;
 * </pre>
 * 
 * @author ralf
 */
public class XMLRoot extends XMLObject {
    Element root = null;

    public XMLRoot(final Document pMetaDoc) {
	document = pMetaDoc;
	root = document.getRootElement();
    }

    public XMLRoot(final List<Long> children) {
	// <root> (root element)
	root = new Element("root");
	// create document
	document = new Document(root);

	// <children>
	final Element e = new Element("children");
	root.addContent(e);
	// <child>
	if (!children.isEmpty()) {
	    for (final Iterator iter = children.iterator(); iter.hasNext();) {
		final Long id = (Long) iter.next();
		final Element el = new Element("child");
		el.setAttribute("id", String.valueOf(id));
		e.addContent(el);
	    }
	}
    }

    public List<Long> getChildren() {
	final List<Long> result = new ArrayList<Long>();
	final Element children = root.getChild("children");
	if (children != null && children.getChildren() != null) {
	    final List childList = children.getChildren();
	    for (final Iterator iter = childList.iterator(); iter.hasNext();) {
		final Element child = (Element) iter.next();
		final Long id = new Long(child.getAttributeValue("id"));
		result.add(id);
	    }
	}
	return result;
    }

    public List getImages() {
	final ArrayList result = new ArrayList();
	final Element images = root.getChild("images");
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
	final Element subcategories = root.getChild("children");
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
	final Element texts = root.getChild("texts");
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
	final Element videos = root.getChild("videos");
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

package com.datazuul.commons.cms.backend.filesystem.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.Text;

/**
 * <pre>
 * &lt;mediaobject author=&quot;1&quot;&gt;
 *   &lt;categories&gt;
 *     &lt;category id=&quot;17&quot; /&gt;
 *     &lt;category id=&quot;101&quot; /&gt;
 *   &lt;/categories&gt;
 *   &lt;title&gt;Title / alternative Text&lt;/title&gt;
 *   &lt;description&gt;
 *     &lt;para&gt;The describing text.&lt;/para&gt;
 *   &lt;/description&gt;
 *   &lt;format&gt;pdf&lt;/format&gt;
 * &lt;/mediaobject&gt;
 * </pre>
 * 
 * @author ralf
 */
public class XMLText extends XMLObject {
    private final Element mediaobject;

    public XMLText(final Document pMetaDoc) {
	document = pMetaDoc;
	mediaobject = document.getRootElement();
    }

    public XMLText(final Text pText) {
	// <mediaobject> (root element)
	mediaobject = new Element("mediaobject");
	// create document
	document = new Document(mediaobject);

	if (pText.getAuthor() != null) {
	    mediaobject.setAttribute("author", String.valueOf(pText.getAuthor().getId()));
	}

	// <categories>
	Element e = new Element("categories");
	mediaobject.addContent(e);
	// <category>
	if (!pText.getCategories().isEmpty()) {
	    for (final Iterator iter = pText.getCategories().iterator(); iter.hasNext();) {
		final Category pCategory = (Category) iter.next();
		final Element el = new Element("category");
		el.setAttribute("id", String.valueOf(pCategory.getId()));
		e.addContent(el);
	    }
	}

	// <title>
	if (pText.getTitle() != null) {
	    e = new Element("title");
	    e.setText(pText.getTitle());
	    mediaobject.addContent(e);
	}

	// <description>
	if (pText.getDescription() != null) {
	    e = new Element("description");
	    // <para>
	    final Element para = new Element("para");
	    para.setText(pText.getDescription());
	    e.addContent(para);
	    mediaobject.addContent(e);
	}

	// <format>
	if (pText.getFormat() != null) {
	    e = new Element("format");
	    e.setText(pText.getFormat());
	    mediaobject.addContent(e);
	}

    }

    public Author getAuthor() {
	Author result = null;
	final String id = mediaobject.getAttributeValue("author");
	if (id != null) {
	    result = new Author(Long.parseLong(id));
	}
	return result;
    }

    public List getCategories() {
	final ArrayList result = new ArrayList();
	final Element categories = mediaobject.getChild("categories");
	if (categories != null && categories.getChildren() != null) {
	    final List categoryList = categories.getChildren();
	    for (final Iterator iter = categoryList.iterator(); iter.hasNext();) {
		final Element category = (Element) iter.next();
		final long id = Long.parseLong(category.getAttributeValue("id"));
		result.add(new Category(id));
	    }
	}
	return result;
    }

    public String getTitle() {
	return mediaobject.getChildText("title");
    }

    public String getDescription() {
	String result = null;
	final Element description = mediaobject.getChild("description");
	if (description != null) {
	    result = description.getChildText("para");
	}
	return result;
    }

    public String getFormat() {
	return mediaobject.getChildText("format");
    }
}

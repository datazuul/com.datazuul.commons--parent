package com.datazuul.commons.cms.backend.filesystem.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.ImageProperties;

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
 *   &lt;imageobject audience=&quot;thumbnail&quot; &gt;
 *     &lt;imagedata align=&quot;right&quot; valign=&quot;top&quot; width=&quot;100px&quot; depth=&quot;37px&quot; format=&quot;PNG&quot;
 *            fileref=&quot;figures/duck_thumbnail.png&quot;/&gt;
 *   &lt;/imageobject&gt;
 *   &lt;imageobject audience=&quot;preview&quot; &gt;
 *     &lt;imagedata align=&quot;right&quot; valign=&quot;top&quot; width=&quot;250px&quot; depth=&quot;92px&quot; format=&quot;PNG&quot;
 *            fileref=&quot;figures/duck_preview.png&quot;/&gt;
 *   &lt;/imageobject&gt;
 *   &lt;imageobject audience=&quot;original&quot; &gt;
 *     &lt;imagedata align=&quot;right&quot; valign=&quot;top&quot; width=&quot;1280px&quot; depth=&quot;460px&quot; format=&quot;PNG&quot;
 *            fileref=&quot;figures/duck.png&quot;/&gt;
 *   &lt;/imageobject&gt;
 * &lt;/mediaobject&gt;
 * </pre>
 * 
 * @author ralf
 */
public class XMLImage extends XMLObject {
    private final Element mediaobject;

    public XMLImage(final Document pMetaDoc) {
	document = pMetaDoc;
	mediaobject = document.getRootElement();
    }

    public XMLImage(final Image pImage) {
	// <mediaobject> (root element)
	mediaobject = new Element("mediaobject");
	// create document
	document = new Document(mediaobject);

	if (pImage.getAuthor() != null) {
	    mediaobject.setAttribute("author", String.valueOf(pImage.getAuthor().getId()));
	}

	// <categories>
	Element e = new Element("categories");
	mediaobject.addContent(e);
	// <category>
	if (!pImage.getCategories().isEmpty()) {
	    for (final Iterator iter = pImage.getCategories().iterator(); iter.hasNext();) {
		final Category pCategory = (Category) iter.next();
		final Element el = new Element("category");
		el.setAttribute("id", String.valueOf(pCategory.getId()));
		e.addContent(el);
	    }
	}

	// <title>
	if (pImage.getTitle() != null) {
	    e = new Element("title");
	    e.setText(pImage.getTitle());
	    mediaobject.addContent(e);
	}

	// <description>
	if (pImage.getDescription() != null) {
	    e = new Element("description");
	    // <para>
	    final Element para = new Element("para");
	    para.setText(pImage.getDescription());
	    e.addContent(para);
	    mediaobject.addContent(e);
	}

	// <imageobject audience="thumbnail">
	mediaobject.addContent(getImageObjectElement(pImage, pImage.getPropsThumbnail()));

	// <imageobject audience="preview">
	mediaobject.addContent(getImageObjectElement(pImage, pImage.getPropsPreview()));

	// <imageobject audience="original">
	mediaobject.addContent(getImageObjectElement(pImage, pImage.getPropsOriginal()));

    }

    private Element getImageObjectElement(final Image image, final ImageProperties propsImage) {
	// * &lt;imageobject audience="thumbnail" &gt;
	// * &lt;imagedata align="right" valign="top" width="100px" depth="37px"
	// format="jpeg"
	// * fileref="2_thumbnail.jpeg"/&gt;
	// * &lt;/imageobject&gt;
	final Element imageobject = new Element("imageobject");
	if (propsImage.getId() != null) {
	    imageobject.setAttribute("audience", propsImage.getId());
	}
	final Element imagedata = new Element("imagedata");
	if (image.getAlign() != null) {
	    imagedata.setAttribute("align", image.getAlign());
	}
	if (image.getValign() != null) {
	    imagedata.setAttribute("valign", image.getValign());
	}
	imagedata.setAttribute("fileref", propsImage.getFilename());
	imagedata.setAttribute("format", image.getFormat());
	imagedata.setAttribute("depth", propsImage.getHeight() + "px");
	imagedata.setAttribute("width", propsImage.getWidth() + "px");

	imageobject.addContent(imagedata);
	return imageobject;
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

    public String getAlign() {
	String result = null;
	final Element props = getImagedata("original");
	if (props != null) {
	    result = props.getAttributeValue("align");
	}
	return result;
    }

    public String getFormat() {
	String result = null;
	final Element props = getImagedata("original");
	if (props != null) {
	    result = props.getAttributeValue("format");
	}
	return result;
    }

    public String getValign() {
	String result = null;
	final Element props = getImagedata("original");
	if (props != null) {
	    result = props.getAttributeValue("valign");
	}
	return result;
    }

    private Element getImagedata(final String audience) {
	Element result = null;
	final List list = mediaobject.getChildren("imageobject");
	for (final Iterator iter = list.iterator(); iter.hasNext();) {
	    final Element imageobject = (Element) iter.next();
	    if (imageobject.getAttributeValue("audience").equals(audience)) {
		result = imageobject.getChild("imagedata");
		break;
	    }
	}
	return result;
    }

    public ImageProperties getPropsOriginal() {
	return getProps("original");
    }

    public ImageProperties getPropsPreview() {
	return getProps("preview");
    }

    public ImageProperties getPropsThumbnail() {
	return getProps("thumbnail");
    }

    private ImageProperties getProps(final String id) {
	ImageProperties result = null;
	final Element imagedata = getImagedata(id);
	if (imagedata != null) {
	    result = new ImageProperties(id);
	    result.setFilename(imagedata.getAttributeValue("fileref"));
	    String height = imagedata.getAttributeValue("depth");
	    // cut "px" from end
	    height = height.substring(0, height.length() - 2);
	    result.setHeight(Integer.parseInt(height));

	    String width = imagedata.getAttributeValue("width");
	    // cut "px" from end
	    width = width.substring(0, width.length() - 2);
	    result.setWidth(Integer.parseInt(width));
	}
	return result;
    }
}

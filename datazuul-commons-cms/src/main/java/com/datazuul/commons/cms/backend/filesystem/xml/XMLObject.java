package com.datazuul.commons.cms.backend.filesystem.xml;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

public class XMLObject {
	protected Document document = null;

	public String toString() {
		String result = null;
		XMLOutputter outputter = new XMLOutputter();
		StringWriter writer = new StringWriter();
		try {
			outputter.output(document, writer);
			writer.close();
			result = writer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}

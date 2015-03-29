package com.datazuul.commons.cms.backend.search.lucene;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;

public class DefaultLuceneDocumentFactory implements LuceneDocumentFactory {

    public Document createDocument(final Object obj) {
	final ClassConfiguration classConfig = getClassConfiguration(obj);
	return createDocumentForObjectFromClassConfiguration(obj, classConfig);
    }

    private Document createDocumentForObjectFromClassConfiguration(final Object obj,
	    final ClassConfiguration class_config) {
	final Iterator iter = class_config.getFieldConfigurations().iterator();
	final Document doc = new Document();

	// add type of object to index to make it possible to search for special
	// object types
	// (e.g. category, article, images, videos, ...)
	final Field objType = new Field("type", obj.getClass().getName(), Field.Store.NO, Field.Index.UN_TOKENIZED);
	doc.add(objType);

	// for each FieldConfiguration
	while (iter.hasNext()) {
	    final FieldConfiguration fieldConfiguration = (FieldConfiguration) iter.next();

	    final String fieldType = fieldConfiguration.getType();
	    String fieldValue = getStringContentOfAttribute(obj, fieldConfiguration.getAttributeName());
	    final String fieldName = fieldConfiguration.getFieldName();

	    Field field = null;

	    if (fieldType.equals(FieldConfiguration.TYPE_TEXT)) {
		// maybe it is html content...
		// extract pure text
		final Parser parser = new Parser();
		try {
		    parser.setInputHTML(fieldValue);
		    final TextExtractingVisitor visitor = new TextExtractingVisitor();
		    parser.visitAllNodesWith(visitor);
		    fieldValue = visitor.getExtractedText();
		} catch (final ParserException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.TOKENIZED);
	    } else if (fieldType.equals(FieldConfiguration.TYPE_KEYWORD)) {
		field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.UN_TOKENIZED);
	    } else if (fieldType.equals(FieldConfiguration.TYPE_UNINDEXED)) {
		field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.NO);
	    } else if (fieldType.equals(FieldConfiguration.TYPE_UNSTORED)) {
		field = new Field(fieldName, fieldValue, Field.Store.NO, Field.Index.TOKENIZED);
	    } else if (fieldType.equals(FieldConfiguration.TYPE_HANDLE)) {
		// contains until now only the id. to be unique add
		// type of object. otherwise entries with same id but different
		// object
		// will collide
		fieldValue = obj.getClass().getSimpleName() + "." + fieldValue;
		field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.UN_TOKENIZED);
	    } else {
		throw new LuceneException("Unknown type for a field, fieldName=" + fieldConfiguration.getFieldName());
	    }

	    doc.add(field);
	}

	return doc;
    }

    private ClassConfiguration getClassConfiguration(final Object obj) {
	final String className = obj.getClass().getName();
	final String configFileName = className.replace('.', '/') + ".lucene.xml";
	final ClassConfiguration newClassConfig = new ClassConfiguration();

	// create the Digester object and add the necessary rules to it
	final Digester digester = new Digester();
	digester.push(newClassConfig);
	digester.addObjectCreate("configuration/field", FieldConfiguration.class.getName());
	digester.addSetProperties("configuration/field");
	digester.addSetNext("configuration/field", "addFieldConfiguration", FieldConfiguration.class.getName());

	// load the xml file
	final InputStream configXml = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(configFileName);
	try {
	    // parse the xml file and fill the new_class_config object
	    digester.parse(new InputStreamReader(configXml));
	    return newClassConfig;
	} catch (final Exception e) {
	    throw new LuceneException("Couldn't load lucene config file successfully, file=" + configFileName, e);
	}
    }

    private static String getStringContentOfAttribute(final Object obj, final String attributeName) {
	try {
	    final String str = BeanUtils.getProperty(obj, attributeName);

	    return (str == null) ? "" : str;
	} catch (final Exception e) {
	    throw new LuceneException("Couldn't get string content of attribute, attributeName=" + attributeName);
	}
    }

    public static final class ClassConfiguration {
	private final List fieldConfigurations = new ArrayList();

	public void addFieldConfiguration(final FieldConfiguration fieldConfiguration) {
	    fieldConfigurations.add(fieldConfiguration);
	}

	public List getFieldConfigurations() {
	    return fieldConfigurations;
	}

	private FieldConfiguration getHandleField() {
	    for (int i = 0; i < fieldConfigurations.size(); i++) {
		final FieldConfiguration fieldConfiguration = (FieldConfiguration) fieldConfigurations.get(i);

		if (fieldConfiguration.getType().equals(FieldConfiguration.TYPE_HANDLE)) {
		    return fieldConfiguration;
		}
	    }

	    throw new LuceneException("No handle field found.");
	}

	@Override
	public boolean equals(final Object obj) {
	    return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
	    return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}

    }

    public Analyzer createAnalyzer() {
	// return new DefaultAnalyzer();
	// return new StandardAnalyzer();
	return new GermanAnalyzer();
    }

    public String getHandleAttributeName(final Object obj) {
	final ClassConfiguration class_config = getClassConfiguration(obj);

	return class_config.getHandleField().getAttributeName();
    }

    public String getHandleFieldName(final Object obj) {
	final ClassConfiguration class_config = getClassConfiguration(obj);

	return class_config.getHandleField().getFieldName();
    }

    // ============= Classes =============

    public static final class FieldConfiguration {
	public static final String TYPE_TEXT = "Text";

	public static final String TYPE_KEYWORD = "Keyword";

	public static final String TYPE_UNINDEXED = "UnIndexed";

	public static final String TYPE_UNSTORED = "UnStored";

	public static final String TYPE_HANDLE = "Handle";

	private String type;

	private String fieldName;

	private String attributeName;

	public String getType() {
	    return type;
	}

	public void setType(final String type) {
	    this.type = type;
	}

	public String getFieldName() {
	    return fieldName;
	}

	public void setFieldName(final String fieldName) {
	    this.fieldName = fieldName;
	}

	public String getAttributeName() {
	    return attributeName;
	}

	public void setAttributeName(final String attributeName) {
	    this.attributeName = attributeName;
	}

	@Override
	public boolean equals(final Object obj) {
	    return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
	    return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
    }

    public static class DefaultAnalyzer extends Analyzer {
	@Override
	public TokenStream tokenStream(final String fieldName, final Reader reader) {
	    final LetterTokenizer tokenizer = new LetterTokenizer(reader);
	    TokenStream result = null;
	    result = new LowerCaseFilter(tokenizer);
	    // result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS);
	    result = new StopFilter(result, GermanAnalyzer.GERMAN_STOP_WORDS);
	    // result = new PorterStemFilter(result);
	    result = new GermanStemFilter(result);

	    return result;
	}
    }
}

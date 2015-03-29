package com.datazuul.commons.cms.backend.search.lucene;

import java.io.IOException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.datazuul.commons.cms.backend.Indexer;
import com.datazuul.commons.cms.domain.DomainName;

public class LuceneIndexer implements Indexer {
    private LuceneIndexNetCMS indexCMS;

    private LuceneDocumentFactory luceneDocumentFactory;

    public void setIndex(final LuceneIndexNetCMS indexCMS) {
	this.indexCMS = indexCMS;
    }

    public void setLuceneDocumentFactory(final LuceneDocumentFactory luceneDocumentFactory) {
	this.luceneDocumentFactory = luceneDocumentFactory;
    }

    public synchronized void index(final DomainName dn, final Object obj) {
	try {
	    unIndex(dn, obj);
	} catch (final RuntimeException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	try {
	    final Analyzer analyzer = createAnalyzer();
	    final IndexWriter writer = indexCMS.createWriter(analyzer);

	    try {
		final Document doc = luceneDocumentFactory.createDocument(obj);
		writer.addDocument(doc);
		writer.optimize();
	    } finally {
		writer.close();
	    }
	} catch (final IOException e) {
	    throw new LuceneException("Cannot update index", e);
	}
    }

    private Analyzer createAnalyzer() {
	// return new StandardAnalyzer();
	return new GermanAnalyzer();
    }

    public synchronized void unIndex(final DomainName dn, final Object obj) {
	final String handleAttributeName = luceneDocumentFactory.getHandleAttributeName(obj);
	final String handleFieldName = luceneDocumentFactory.getHandleFieldName(obj);
	String handleAttributeValue = null;

	try {
	    handleAttributeValue = BeanUtils.getProperty(obj, handleAttributeName);
	    handleAttributeValue = obj.getClass().getSimpleName() + "." + handleAttributeValue;
	} catch (final Exception e) {
	    throw new LuceneException("Cannot identify object", e);
	}

	try {
	    final IndexReader reader = indexCMS.createReader();

	    try {
		final Term t = new Term(handleFieldName, handleAttributeValue);
		reader.deleteDocuments(t);
	    } finally {
		reader.close();
	    }
	} catch (final IOException e) {
	    throw new LuceneException("Cannot delete from index", e);
	}
    }

    public boolean isIndexEmpty(final DomainName dn) {
	try {
	    return this.indexCMS.getNumDocs() == 0;
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}
    }

    public void deleteIndex(final DomainName dn) throws Exception {
	this.indexCMS.delete();

    }
}

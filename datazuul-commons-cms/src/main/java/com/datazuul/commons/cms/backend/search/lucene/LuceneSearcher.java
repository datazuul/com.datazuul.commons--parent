package com.datazuul.commons.cms.backend.search.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;

import com.datazuul.commons.cms.backend.SearchResult;
import com.datazuul.commons.cms.backend.Searcher;
import com.datazuul.commons.cms.domain.DomainName;

public class LuceneSearcher implements Searcher {
    private LuceneIndexNetCMS indexCMS;

    private LuceneDocumentFactory luceneDocumentFactory;

    public void setIndex(final LuceneIndexNetCMS indexCMS) {
	this.indexCMS = indexCMS;
    }

    public void setLuceneDocumentFactory(final LuceneDocumentFactory luceneDocumentFactory) {
	this.luceneDocumentFactory = luceneDocumentFactory;
    }

    public List search(final DomainName dn, final String query, final Object searchableObject) {
	MultiFieldQueryParser qp = null;
	Query myQuery = null;
	Analyzer analyzer = null;

	// create search filter for specific search object
	final Filter[] filters = new Filter[1];
	final Query typeQuery = new TermQuery(new Term("type", searchableObject.getClass().getName()));
	filters[0] = new QueryFilter(typeQuery);
	final Filter filter = new ChainedFilter(filters, ChainedFilter.OR);

	// get fields to do full text search on
	final ArrayList fields = new ArrayList();
	final Document objDoc = luceneDocumentFactory.createDocument(searchableObject);
	final List objFields = objDoc.getFields();
	for (final Iterator iter = objFields.iterator(); iter.hasNext();) {
	    final Field objField = (Field) iter.next();
	    if (objField.isIndexed()) {
		fields.add(objField.name());
	    }
	}
	final String[] searchFields = (String[]) fields.toArray(new String[fields.size()]);

	// parse query string
	try {
	    analyzer = luceneDocumentFactory.createAnalyzer();
	    qp = new MultiFieldQueryParser(searchFields, analyzer);
	    qp.setDefaultOperator(Operator.OR);
	    myQuery = qp.parse(query.toLowerCase()); // lucene seems to index
	    // all in lower case...
	} catch (final Exception e) {
	    throw new LuceneException("Couldn't parse the query: " + e.getMessage());
	}

	// do search
	IndexSearcher searcher = null;
	try {
	    searcher = indexCMS.createSearcher();

	    // do search
	    final Hits hits = searcher.search(myQuery, filter);

	    // highlight text fragments in hits
	    final Highlighter highlighter = new Highlighter(new QueryScorer(myQuery));
	    highlighter.setTextFragmenter(new SimpleFragmenter(100));

	    final List result = new ArrayList(hits.length());
	    for (int i = 0; i < hits.length(); i++) {
		final Document doc = hits.doc(i);

		// highlight
		final StringBuffer highlightedTextSB = new StringBuffer();
		for (int j = 0; j < searchFields.length; j++) {
		    final String fieldName = searchFields[j];
		    final String text = doc.get(fieldName);
		    if (text != null) {
			final TokenStream tokenStream = analyzer.tokenStream(fieldName, new StringReader(text));
			// get 3 best fragments and seperate them with "..."
			final String textFragment = highlighter.getBestFragments(tokenStream, text, 3, "...");
			if (textFragment != null && textFragment.length() > 0) {
			    if (highlightedTextSB.length() > 0) {
				highlightedTextSB.append("<br>");
			    }
			    highlightedTextSB.append(textFragment);
			}
		    }
		}
		final String highlightedText = highlightedTextSB.toString();

		// add SearchResult object to result-list
		final SearchResult hitObject = new SearchResult();

		String id = doc.get("handle");
		// cut object class type from id:
		id = id.substring(id.lastIndexOf(".") + 1);

		hitObject.setHandle(Long.valueOf(id));
		hitObject.setHighlightedText(highlightedText);
		result.add(hitObject);
		// result.add(Long.valueOf(doc.get("handle")));
	    }
	    return result;
	} catch (final Exception e) {
	    throw new LuceneException("Couldn't complete search", e);
	} finally {
	    try {
		if (searcher != null) {
		    searcher.close();
		}
	    } catch (final IOException e) {
		throw new LuceneException("Couldn't complete search", e);
	    }
	}
    }
}

package com.datazuul.commons.cms.backend.search.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexNetCMS {
    // FIXME: create an index per domain!
    private static final String DEFAULT_INDEX_DIR = "index";

    private final String indexDir;

    private IndexWriter indexWriter;

    private LuceneIndexNetCMS() {
	this(DEFAULT_INDEX_DIR);
    }

    public LuceneIndexNetCMS(final String indexDir) {
	this.indexDir = indexDir;
	init();
    }

    private void init() {
	// create index-directory if not exists
	if (indexDir != null) {
	    final File index = new File(indexDir);
	    if (!index.exists()) {
		try {
		    new IndexWriter(getDirectory(true), null, true).close();
		} catch (final IOException e) {
		    throw new LuceneException("Cannot create index directory '" + indexDir + "'", e);
		}
	    }
	}
    }

    public IndexReader createReader() throws IOException {
	IndexReader ir = null;
	try {
	    ir = IndexReader.open(getDirectory(false));
	} catch (final FileNotFoundException e) {
	    // TODO reindex ???
	    e.printStackTrace();
	}
	return ir;
    }

    public IndexWriter createWriter(final Analyzer analyzer) throws IOException {
	return new IndexWriter(getDirectory(false), analyzer, false);
    }

    public IndexSearcher createSearcher() throws IOException {
	return new IndexSearcher(getDirectory(false));
    }

    public int getNumDocs() throws IOException {
	final IndexReader reader = createReader();
	final int result = reader.numDocs();
	reader.close();
	return result;
    }

    protected Directory getDirectory(final boolean create) throws IOException {
	return FSDirectory.getDirectory(indexDir, create);
    }

    public boolean isEmpty() {
	final File index = new File(indexDir);
	if (index.exists() && index.list() != null && index.list().length > 0) {
	    return false;
	}
	return true;
    }

    public void delete() throws IOException {
	final File index = new File(indexDir);
	FileUtils.deleteDirectory(index);
	// init again to have basis for index
	init();
    }
}

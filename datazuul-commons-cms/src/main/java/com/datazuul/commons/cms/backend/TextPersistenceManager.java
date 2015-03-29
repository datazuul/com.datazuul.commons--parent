package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Text;

public abstract class TextPersistenceManager {
    public void save(final DomainName dn, final Text text) {
	saveBinary(dn, text);
	saveMetadata(dn, text);
    }

    public Text load(final DomainName dn, final Text text) {
	final Text result = loadMetadata(dn, text);
	if (result != null) {
	    loadBinary(dn, result);
	}
	return result;
    }

    protected abstract void loadBinary(DomainName dn, Text text);

    protected abstract Text loadMetadata(DomainName dn, Text text);

    /**
     * Save binary content of text.
     * 
     * @param text
     *            text to save
     */
    protected abstract void saveBinary(DomainName dn, Text text);

    /**
     * Save meta data.
     * 
     * @param text
     *            text to save
     */
    protected abstract void saveMetadata(DomainName dn, Text text);
}

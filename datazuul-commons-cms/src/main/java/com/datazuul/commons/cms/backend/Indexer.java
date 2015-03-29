package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.DomainName;

public interface Indexer {
	void index(DomainName dn, Object obj);

	boolean isIndexEmpty(DomainName dn);

	void deleteIndex(DomainName dn) throws Exception;

	void unIndex(DomainName dn, Object obj);
}

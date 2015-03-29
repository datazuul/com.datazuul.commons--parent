package com.datazuul.commons.cms.backend;

import java.util.List;

import com.datazuul.commons.cms.domain.DomainName;

public interface Searcher {
	/**
	 * Perform the search operation and returns a list of found items.
	 * 
	 * @param query
	 *            string containing search keywords (and operands)
	 * @param searchableObject
	 *            searchable object that has been indexed and now should be used
	 *            in search
	 * @return list of found items
	 */
	List search(DomainName dn, String query, Object searchableObject);
}

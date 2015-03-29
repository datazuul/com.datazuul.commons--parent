package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;

public abstract class CategoryPersistenceManager {
	public void save(final DomainName dn, final Category category) {
		saveMetadata(dn, category);
	}

	public Category load(DomainName dn, Category category) {
		return loadMetadata(dn, category);
	}

	protected abstract Category loadMetadata(DomainName dn, Category category);

	/**
	 * Save meta data.
	 * 
	 * @param category
	 *            category to save
	 */
	protected abstract void saveMetadata(final DomainName dn,
			final Category category);

}

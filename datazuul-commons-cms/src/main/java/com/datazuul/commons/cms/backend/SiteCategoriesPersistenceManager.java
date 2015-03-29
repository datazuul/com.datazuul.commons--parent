package com.datazuul.commons.cms.backend;

import java.util.List;

import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;

public abstract class SiteCategoriesPersistenceManager {
	public abstract List<Category> load(DomainName dn);

	public abstract void save(final DomainName dn,
			final List<Category> categories);
}

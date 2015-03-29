package com.datazuul.commons.cms.business;

import java.util.Iterator;
import java.util.List;

import com.datazuul.commons.cms.backend.NetCMSRepository;
import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;

/**
 * @author ralf
 */
public class CategoryService {
    public void delete(final DomainName dn, final Category category,
	    final boolean recursively) {
	// delete all subcategories of category recursively
	final List subcategories = category.getSubcategories();
	if (recursively == true && subcategories != null
		&& !subcategories.isEmpty()) {
	    for (final Iterator iterator = subcategories.iterator(); iterator
		    .hasNext();) {
		Category subcat = (Category) iterator.next();
		subcat = load(dn, subcat.getId());
		delete(dn, subcat, true);
	    }
	}

	// delete all articles of category
	final List articles = category.getArticles();
	if (articles != null && !articles.isEmpty()) {
	    for (final Iterator iterator = articles.iterator(); iterator
		    .hasNext();) {
		Article article = (Article) iterator.next();
		final ArticleService as = new ArticleService();
		article = as.load(dn, article.getId());
		as.delete(dn, article);
	    }
	}

	// delete all images of category
	final List images = category.getImages();
	if (images != null && !images.isEmpty()) {
	    for (final Iterator iterator = images.iterator(); iterator
		    .hasNext();) {
		Image image = (Image) iterator.next();
		final ImageService is = new ImageService();
		image = is.load(dn, image.getId());
		is.delete(dn, image);
	    }
	}

	// delete category from parent
	Category parent = category.getParent();
	parent = load(dn, parent.getId());
	parent.removeChild(category);
	save(dn, parent);

	// at least delete category itself
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	repository.deleteCategory(dn, category);
	repository.unIndex(dn, category);
    }

    public Category load(final DomainName dn, final long id) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	return repository.getCategoryById(dn, id);
    }

    public void save(final DomainName dn, final Category parent) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	repository.save(dn, parent);
    }
}

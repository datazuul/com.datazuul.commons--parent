package com.datazuul.commons.cms.business;

import com.datazuul.commons.cms.backend.NetCMSRepository;
import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.DomainName;

/**
 * @author Ralf Eichinger
 */
public class ArticleService {

    public void delete(final DomainName dn, final Article article) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	repository.deleteArticle(dn, article);
	repository.unIndex(dn, article);
    }

    public Article load(final DomainName dn, final long id) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	return repository.getArticleById(dn, id);
    }

    public void save(final DomainName dn, final Article article) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	repository.save(dn, article);
    }
}

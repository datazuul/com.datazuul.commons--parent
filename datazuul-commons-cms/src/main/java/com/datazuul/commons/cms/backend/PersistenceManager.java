package com.datazuul.commons.cms.backend;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;

public interface PersistenceManager extends Serializable {
    void setUseCache(boolean useCache);

    boolean useCache();

    /**
     * Contained Objects in object are stored too (recursive).
     * 
     * @param obj
     *            object to store into storage
     */
    void save(DomainName dn, Object obj);

    List findAll(DomainName dn, Class cls);

    /**
     * @param cls
     *            class of object to load from storage
     * @param id
     *            id of object
     * @return object with loaded data (for performance and size reasons
     *         recursive objects not loaded)
     */
    Object getById(DomainName dn, Class cls, long id);

    /**
     * @param obj
     *            object to load from storage
     * @return object with loaded data (for performance and size reasons
     *         recursive objects not loaded)
     */
    Object load(DomainName dn, Object obj);

    void delete(DomainName dn, Object obj);

    long getLastId(DomainName dn, Class cls);

    File getPreviewFile(DomainName dn, Object obj);

    File getThumbnailFile(DomainName dn, Object obj);

    public List<Category> getRootCategories(DomainName dn);

    void saveRootCategories(final DomainName dn, final List<Category> categories);

    public List<Category> getSiteCategories(DomainName dn);

    void saveSiteCategories(DomainName dn, List<Category> categories);
}

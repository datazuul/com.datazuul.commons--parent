package com.datazuul.commons.cms.backend.database.hibernate;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import com.datazuul.commons.cms.backend.PersistenceException;
import com.datazuul.commons.cms.backend.PersistenceManager;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;

public class HibernatePersistenceManager implements PersistenceManager {
    private Session session;

    public void init() {
	try {
	    // Load Hibernate configuration
	    final Configuration config = new Configuration();
	    config.configure();

	    // update database schema if required
	    new SchemaUpdate(config).execute(true, true);

	    final SessionFactory sessionFactory = config.buildSessionFactory();
	    session = sessionFactory.openSession();
	} catch (final HibernateException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public Connection getConnection() throws HibernateException {
	return session.connection();
    }

    public void dispose() {
	try {
	    try {
		session.flush();
		session.connection().commit();
	    } finally {
		session.close();
	    }
	} catch (final Exception e) {
	    throw new PersistenceException("Could not close the session", e);
	}
    }

    public void save(final Object obj) {
	try {
	    session.saveOrUpdate(obj);
	    session.flush();
	} catch (final HibernateException e) {
	    throw new RuntimeException("Could not persist object: " + e.getMessage(), e);
	}
    }

    public List findAll(final Class cls) {
	List objects = null;
	try {

	    final Query query = session.createQuery("FROM articles IN CLASS " + cls.getName());
	    objects = query.list();

	} catch (final HibernateException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

	return objects;
    }

    public Object getById(final Class cls, final long id) {
	try {
	    return session.load(cls, new Long(id));
	} catch (final ObjectDeletedException e) {
	    return null;
	} catch (final HibernateException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public void delete(final Object obj) {
	try {
	    session.delete(obj);
	} catch (final HibernateException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public long getLastId(final Class cls) {
	// TODO return last id
	return 0;
    }

    public Object load(final Object obj) {
	// TODO Auto-generated method stub
	return null;
    }

    public void setUseCache(final boolean useCache) {
	// TODO Auto-generated method stub

    }

    public boolean useCache() {
	// TODO Auto-generated method stub
	return false;
    }

    public List<Category> getRootCategories() {
	// TODO Auto-generated method stub
	return null;
    }

    public void saveRootCategories(final List<Category> rootCategories) {
	// TODO Auto-generated method stub

    }

    public List<Category> getSiteCategories() {
	// TODO Auto-generated method stub
	return null;
    }

    public void saveSiteCategories(final List<Category> categories) {
	// TODO Auto-generated method stub

    }

    public void delete(final DomainName dn, final Object obj) {
	// TODO Auto-generated method stub

    }

    public List findAll(final DomainName dn, final Class cls) {
	// TODO Auto-generated method stub
	return null;
    }

    public Object getById(final DomainName dn, final Class cls, final long id) {
	// TODO Auto-generated method stub
	return null;
    }

    public long getLastId(final DomainName dn, final Class cls) {
	// TODO Auto-generated method stub
	return 0;
    }

    public List<Category> getRootCategories(final DomainName dn) {
	// TODO Auto-generated method stub
	return null;
    }

    public List<Category> getSiteCategories(final DomainName dn) {
	// TODO Auto-generated method stub
	return null;
    }

    public Object load(final DomainName dn, final Object obj) {
	// TODO Auto-generated method stub
	return null;
    }

    public void save(final DomainName dn, final Object obj) {
	// TODO Auto-generated method stub

    }

    public void saveRootCategories(final DomainName dn, final List<Category> categories) {
	// TODO Auto-generated method stub

    }

    public void saveSiteCategories(final DomainName dn, final List<Category> categories) {
	// TODO Auto-generated method stub

    }

    public File getPreviewFile(final DomainName dn, final Object obj) {
	// TODO Auto-generated method stub
	return null;
    }

    public File getThumbnailFile(final DomainName dn, final Object obj) {
	// TODO Auto-generated method stub
	return null;
    }
}

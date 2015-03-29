package com.datazuul.commons.cms.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.commons.cms.domain.Video;

public class NetCMSRepository {
    private PersistenceManager pm;

    private static HashMap<String, Searcher> searcherMap = new HashMap<String, Searcher>();

    private static HashMap<String, Indexer> indexerMap = new HashMap<String, Indexer>();

    private static NetCMSRepository _instance;

    private NetCMSRepository() {
    }

    public static NetCMSRepository getInstance() {
	if (_instance == null) {
	    _instance = new NetCMSRepository();
	}
	return _instance;
    }

    // init repository if needed
    public void init(final DomainName dn) {
	// TODO: get init values (author, titles, etc.) from web.xml
	// root category

	// create root category "1"
	Category rootCategory = this.getCategoryById(dn, 1);
	if (rootCategory != null) {
	    // init already done before...
	    return;
	} else {
	    rootCategory = new Category(1);
	    rootCategory.setName("Startseite");
	    this.save(dn, rootCategory);
	}

	List rootCategories = this.getRootCategories(dn);
	if (rootCategories == null || rootCategories.isEmpty()) {
	    rootCategories = new ArrayList();
	    rootCategories.add(rootCategory);
	    this.saveRootCategories(dn, rootCategories);
	}

	// master author (admin)
	Author author = this.getAuthorById(dn, 1);
	if (author == null) {
	    // create default author "1"
	    author = new Author(1);
	    author.setFirstname("Ralf");
	    author.setSurname("Eichinger");
	    this.save(dn, author);
	}

	// // root article under root category
	Article rootArticle = this.getArticleById(dn, 1);
	if (rootArticle == null) {
	    // create default article "1"
	    rootArticle = new Article(1);
	    rootArticle.setTitle("Willkommen");
	    rootArticle.setHtmlContent("<h1>Willkommen auf der Startseite!</h1><p>Hier kommt Ihr Text...</p>");
	    rootArticle.setCategory(rootCategory);
	    rootArticle.setAuthor(author);
	    this.save(dn, rootArticle);

	    rootCategory.getArticles().add(rootArticle);
	    this.save(dn, rootCategory);

	    author.getArticles().add(rootArticle);
	    this.save(dn, author);
	}
    }

    public void setPersistenceManager(final PersistenceManager pm) {
	this.pm = pm;
    }

    public void setIndexer(final DomainName dn, final Indexer indexer) {
	indexerMap.put(dn.getFullyQualifiedDomainName(), indexer);
    }

    public void setSearcher(final DomainName dn, final Searcher searcher) {
	searcherMap.put(dn.getFullyQualifiedDomainName(), searcher);
    }

    // common for all objects
    public void save(final DomainName dn, final Object obj) {
	pm.save(dn, obj);
    }

    // Article
    public List getArticles(final DomainName dn) {
	return pm.findAll(dn, Article.class);
    }

    public Article getArticleById(final DomainName dn, final long id) {
	return (Article) pm.getById(dn, Article.class, id);
    }

    public void deleteArticle(final DomainName dn, final Article article) {
	pm.delete(dn, article);
    }

    // Author
    public List getAuthors(final DomainName dn) {
	return pm.findAll(dn, Author.class);
    }

    public Author getAuthorById(final DomainName dn, final long id) {
	return (Author) pm.getById(dn, Author.class, id);
    }

    public void deleteAuthor(final DomainName dn, final Author author) {
	pm.delete(dn, author);
    }

    // Category
    public List getRootCategories(final DomainName dn) {
	return pm.getRootCategories(dn);
    }

    public void saveRootCategories(final DomainName dn, final List<Category> rootCategories) {
	pm.saveRootCategories(dn, rootCategories);
    }

    public List getCategories(final DomainName dn) {
	return pm.findAll(dn, Category.class);
    }

    public Category getCategoryById(final DomainName dn, final long id) {
	return (Category) pm.getById(dn, Category.class, id);
    }

    public void deleteCategory(final DomainName dn, final Category category) {
	pm.delete(dn, category);
    }

    // Image
    public List getImages(final DomainName dn) {
	return pm.findAll(dn, Image.class);
    }

    public Image getImageById(final DomainName dn, final long id) {
	return (Image) pm.getById(dn, Image.class, id);
    }

    public void deleteImage(final DomainName dn, final Image image) {
	pm.delete(dn, image);
    }

    // Text
    public List getTexts(final DomainName dn) {
	return pm.findAll(dn, Text.class);
    }

    public Text getTextById(final DomainName dn, final long id) {
	return (Text) pm.getById(dn, Text.class, id);
    }

    public void deleteText(final DomainName dn, final Text text) {
	pm.delete(dn, text);
    }

    // Video
    public List getVideos(final DomainName dn) {
	return pm.findAll(dn, Video.class);
    }

    public Video getVideoById(final DomainName dn, final long id) {
	return (Video) pm.getById(dn, Video.class, id);
    }

    public void deleteVideo(final DomainName dn, final Video video) {
	pm.delete(dn, video);
    }

    public void index(final DomainName dn, final Object obj) {
	getIndexer(dn).index(dn, obj);
    }

    private Indexer getIndexer(final DomainName dn) {
	return indexerMap.get(dn.getFullyQualifiedDomainName());
    }

    public void unIndex(final DomainName dn, final Object obj) {
	getIndexer(dn).unIndex(dn, obj);
    }

    /**
     * create index for all objects
     * 
     * @param force
     *            true: delete index if present before reindexing, false: check
     *            if present
     */
    public void reIndex(final DomainName dn, final boolean force) {
	final Indexer indexer = getIndexer(dn);
	if (force) {
	    try {
		indexer.deleteIndex(dn);
	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	if (indexer.isIndexEmpty(dn)) {
	    // TODO logging: reindexing...

	    // index articles
	    final long lastArticleId = pm.getLastId(dn, Article.class);
	    for (long i = 1; i <= lastArticleId; i++) {
		final Article article = (Article) pm.getById(dn, Article.class, i);
		if (article != null) {
		    index(dn, article);
		}
	    }

	    // index authors
	    final long lastAuthorId = pm.getLastId(dn, Author.class);
	    for (long i = 1; i <= lastAuthorId; i++) {
		final Author author = (Author) pm.getById(dn, Author.class, i);
		if (author != null) {
		    index(dn, author);
		}
	    }

	    // index categories
	    final long lastCategoryId = pm.getLastId(dn, Category.class);
	    for (long i = 1; i <= lastCategoryId; i++) {
		final Category category = (Category) pm.getById(dn, Category.class, i);
		if (category != null) {
		    index(dn, category);
		}
	    }

	    // index images
	    final long lastImageId = pm.getLastId(dn, Image.class);
	    for (long i = 1; i <= lastImageId; i++) {
		final Image image = (Image) pm.getById(dn, Image.class, i);
		if (image != null) {
		    index(dn, image);
		}
	    }

	    // index texts
	    final long lastTextId = pm.getLastId(dn, Text.class);
	    for (long i = 1; i <= lastTextId; i++) {
		final Text text = (Text) pm.getById(dn, Text.class, i);
		if (text != null) {
		    index(dn, text);
		}
	    }

	    // index videos
	    final long lastVideoId = pm.getLastId(dn, Video.class);
	    for (long i = 1; i <= lastVideoId; i++) {
		final Video video = (Video) pm.getById(dn, Video.class, i);
		if (video != null) {
		    index(dn, video);
		}
	    }
	}
    }

    public List search(final DomainName dn, final String query, final Object searchableObject) {
	return getSearcher(dn).search(dn, query, searchableObject);
    }

    private Searcher getSearcher(final DomainName dn) {
	return searcherMap.get(dn.getFullyQualifiedDomainName());
    }

    public List getLatest(final DomainName dn, final int count, final Class objectClass) {
	final List result = new ArrayList();
	final long lastObjectId = pm.getLastId(dn, objectClass);
	for (long i = lastObjectId; i > (lastObjectId - count); i--) {
	    final Object obj = pm.getById(dn, objectClass, i);
	    if (obj != null) {
		result.add(obj);
	    }
	}
	return result;
    }

    /**
     * @return the pm
     */
    public PersistenceManager getPm() {
	return pm;
    }

    public File getPreviewFile(final DomainName dn, final Object obj) {
	return pm.getPreviewFile(dn, obj);
    }

    public File getThumbnailFile(final DomainName dn, final Object obj) {
	return pm.getThumbnailFile(dn, obj);
    }

    public List getSiteCategories(final DomainName dn) {
	return pm.getSiteCategories(dn);
    }

    public void saveSiteCategories(final DomainName dn, final List<Category> categories) {
	pm.saveSiteCategories(dn, categories);
    }

}

package com.datazuul.commons.cms.backend.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.datazuul.commons.cms.backend.ArticlePersistenceManager;
import com.datazuul.commons.cms.backend.AuthorPersistenceManager;
import com.datazuul.commons.cms.backend.CategoryPersistenceManager;
import com.datazuul.commons.cms.backend.ImagePersistenceManager;
import com.datazuul.commons.cms.backend.PersistenceManager;
import com.datazuul.commons.cms.backend.RootCategoriesPersistenceManager;
import com.datazuul.commons.cms.backend.SiteCategoriesPersistenceManager;
import com.datazuul.commons.cms.backend.TextPersistenceManager;
import com.datazuul.commons.cms.backend.VideoPersistenceManager;
import com.datazuul.commons.cms.domain.Article;
import com.datazuul.commons.cms.domain.Author;
import com.datazuul.commons.cms.domain.Category;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;
import com.datazuul.commons.cms.domain.Text;
import com.datazuul.commons.cms.domain.Video;
import com.datazuul.framework.persistence.file.FileReader;
import com.datazuul.framework.persistence.file.FileWriter;

public class FilePersistenceManager implements PersistenceManager {
    private static FilePersistenceManager _instance = null;

    boolean useCache = false;

    public static final String DEFAULT_ENCODING = "UTF-8";

    private static final String LAST_ID_FILENAME = "lastId.txt";

    private static HashMap ARTICLES = new HashMap();

    private static HashMap AUTHORS = new HashMap();

    private static HashMap CATEGORIES = new HashMap();

    private static HashMap IMAGES = new HashMap();

    private static HashMap ROOT_CATEGORIES = new HashMap();

    private static HashMap SITE_CATEGORIES = new HashMap();

    private static HashMap TEXTS = new HashMap();

    private static HashMap VIDEOS = new HashMap();

    private FilePersistenceManager() {
    }

    public static FilePersistenceManager getInstance(final DomainName dn) {
	if (_instance == null) {
	    _instance = new FilePersistenceManager();
	}
	// init persistence manager if needed
	_instance.init(dn);
	return _instance;
    }

    public void save(final DomainName dn, final Object obj) {
	if (obj instanceof Article) {
	    final Article article = (Article) obj;
	    if (article.getId() == 0) {
		article.setId(getNextId(dn, Article.class));
	    }
	    final ArticlePersistenceManager pm = new FileArticlePersistenceManager();
	    pm.save(dn, article);
	    if (useCache()) {
		ARTICLES.put(getDomainNamePrefix(dn) + article.getId(), article);
	    }
	} else if (obj instanceof Author) {
	    final Author author = (Author) obj;
	    if (author.getId() == 0) {
		author.setId(getNextId(dn, Author.class));
	    }
	    final AuthorPersistenceManager pm = new FileAuthorPersistenceManager();
	    pm.save(dn, author);
	    if (useCache()) {
		AUTHORS.put(getDomainNamePrefix(dn) + author.getId(), author);
	    }
	} else if (obj instanceof Category) {
	    final Category category = (Category) obj;
	    if (category.getId() == 0) {
		category.setId(getNextId(dn, Category.class));
	    }
	    final CategoryPersistenceManager pm = new FileCategoryPersistenceManager();
	    pm.save(dn, category);
	    if (useCache()) {
		CATEGORIES.put(getDomainNamePrefix(dn) + category.getId(), category);
	    }
	} else if (obj instanceof Image) {
	    final Image image = (Image) obj;
	    if (image.getId() == 0) {
		image.setId(getNextId(dn, Image.class));
	    }
	    final ImagePersistenceManager pm = new FileImagePersistenceManager();
	    pm.save(dn, image);
	    if (useCache()) {
		IMAGES.put(getDomainNamePrefix(dn) + image.getId(), image);
	    }
	} else if (obj instanceof Text) {
	    final Text text = (Text) obj;
	    if (text.getId() == 0) {
		text.setId(getNextId(dn, Text.class));
	    }
	    final TextPersistenceManager pm = new FileTextPersistenceManager();
	    pm.save(dn, text);
	    if (useCache()) {
		TEXTS.put(getDomainNamePrefix(dn) + text.getId(), text);
	    }
	} else if (obj instanceof Video) {
	    final Video video = (Video) obj;
	    if (video.getId() == 0) {
		video.setId(getNextId(dn, Video.class));
	    }
	    final VideoPersistenceManager pm = new FileVideoPersistenceManager();
	    pm.save(dn, video);
	    if (useCache()) {
		VIDEOS.put(getDomainNamePrefix(dn) + video.getId(), video);
	    }
	}
    }

    private String getDomainNamePrefix(final DomainName dn) {
	String result = "";
	if (dn != null) {
	    result = dn.getFullyQualifiedDomainName() + "-";
	}
	return result;
    }

    public Object load(final DomainName dn, final Object obj) {
	Object result = null;
	if (obj instanceof Article) {
	    final Article article = (Article) obj;
	    final String id = String.valueOf(article.getId());
	    if (useCache()) {
		result = ARTICLES.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final ArticlePersistenceManager pm = new FileArticlePersistenceManager();
		result = pm.load(dn, article);
		if (result != null) {
		    ARTICLES.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	} else if (obj instanceof Author) {
	    final Author author = (Author) obj;
	    final String id = String.valueOf(author.getId());
	    if (useCache()) {
		result = AUTHORS.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final AuthorPersistenceManager pm = new FileAuthorPersistenceManager();
		result = pm.load(dn, author);
		if (result != null) {
		    AUTHORS.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	} else if (obj instanceof Category) {
	    final Category category = (Category) obj;
	    final String id = String.valueOf(category.getId());
	    if (useCache()) {
		result = CATEGORIES.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final CategoryPersistenceManager pm = new FileCategoryPersistenceManager();
		result = pm.load(dn, category);
		if (result != null) {
		    CATEGORIES.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	} else if (obj instanceof Image) {
	    final Image image = (Image) obj;
	    final String id = String.valueOf(image.getId());
	    if (useCache()) {
		result = IMAGES.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final ImagePersistenceManager pm = new FileImagePersistenceManager();
		result = pm.load(dn, image);
		if (result != null) {
		    IMAGES.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	} else if (obj instanceof Text) {
	    final Text text = (Text) obj;
	    final String id = String.valueOf(text.getId());
	    if (useCache()) {
		result = TEXTS.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final TextPersistenceManager pm = new FileTextPersistenceManager();
		result = pm.load(dn, text);
		if (result != null) {
		    TEXTS.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	} else if (obj instanceof Video) {
	    final Video video = (Video) obj;
	    final String id = String.valueOf(video.getId());
	    if (useCache()) {
		result = VIDEOS.get(getDomainNamePrefix(dn) + id);
	    }
	    if (result == null) {
		final VideoPersistenceManager pm = new FileVideoPersistenceManager();
		result = pm.load(dn, video);
		if (result != null) {
		    VIDEOS.put(getDomainNamePrefix(dn) + id, result);
		}
	    }
	}
	return result;
    }

    public void delete(final DomainName dn, final Object obj) {
	if (obj instanceof Article) {
	    final Article article = (Article) obj;
	    delete(dn, article);
	} else if (obj instanceof Author) {
	    final Author author = (Author) obj;
	    delete(dn, author);
	} else if (obj instanceof Category) {
	    final Category category = (Category) obj;
	    delete(dn, category);
	} else if (obj instanceof Image) {
	    final Image image = (Image) obj;
	    delete(dn, image);
	} else if (obj instanceof Text) {
	    final Text text = (Text) obj;
	    delete(dn, text);
	} else if (obj instanceof Video) {
	    final Video video = (Video) obj;
	    delete(dn, video);
	}
    }

    public Object getById(final DomainName dn, final Class cls, final long id) {
	Object result = null;
	if (cls == Article.class) {
	    result = load(dn, new Article(id));
	} else if (cls == Author.class) {
	    result = load(dn, new Author(id));
	} else if (cls == Category.class) {
	    result = load(dn, new Category(id));
	} else if (cls == Image.class) {
	    result = load(dn, new Image(id));
	} else if (cls == Text.class) {
	    result = load(dn, new Text(id));
	} else if (cls == Video.class) {
	    result = load(dn, new Video(id));
	}
	return result;
    }

    public List findAll(final DomainName dn, final Class cls) {
	final ArrayList result = new ArrayList();
	final long lastId = getLastId(dn, cls);
	for (long i = 1; i <= lastId; i++) {
	    final Object obj = getById(dn, cls, i);
	    if (obj != null) {
		result.add(obj);
	    }
	}
	return result;
    }

    private void init(final DomainName dn) {
	try {
	    // create repository
	    String filepath = FilePathFactory.getInstance().getRootPathArticles(dn);
	    final File repositoryDir = new File(filepath);
	    if (!repositoryDir.exists()) {
		repositoryDir.mkdirs();
	    } else {
		return;
	    }

	    // create lastid.txt files
	    filepath = FilePathFactory.getInstance().getRootPathArticles(dn);
	    createLastIdFile(filepath);

	    filepath = FilePathFactory.getInstance().getRootPathAuthors(dn);
	    createLastIdFile(filepath);

	    filepath = FilePathFactory.getInstance().getRootPathCategories(dn);
	    createLastIdFile(filepath);

	    filepath = FilePathFactory.getInstance().getRootPathImages(dn);
	    createLastIdFile(filepath);

	    filepath = FilePathFactory.getInstance().getRootPathTexts(dn);
	    createLastIdFile(filepath);

	    filepath = FilePathFactory.getInstance().getRootPathVideos(dn);
	    createLastIdFile(filepath);
	} catch (final NullPointerException e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    private void createLastIdFile(final String filepath) {
	final File file = new File(filepath, LAST_ID_FILENAME);
	if (!file.exists()) {
	    FileWriter.writeContent(file, "1", DEFAULT_ENCODING);
	}
    }

    public void dispose() {
	ARTICLES = new HashMap();
	AUTHORS = new HashMap();
	CATEGORIES = new HashMap();
	IMAGES = new HashMap();
	TEXTS = new HashMap();
	VIDEOS = new HashMap();
    }

    public long getNextId(final DomainName dn, final Class cls) {
	long result = 0;
	String filepath = null;
	if (cls == Article.class) {
	    filepath = FilePathFactory.getInstance().getRootPathArticles(dn);
	} else if (cls == Author.class) {
	    filepath = FilePathFactory.getInstance().getRootPathAuthors(dn);
	} else if (cls == Category.class) {
	    filepath = FilePathFactory.getInstance().getRootPathCategories(dn);
	} else if (cls == Image.class) {
	    filepath = FilePathFactory.getInstance().getRootPathImages(dn);
	} else if (cls == Text.class) {
	    filepath = FilePathFactory.getInstance().getRootPathTexts(dn);
	} else if (cls == Video.class) {
	    filepath = FilePathFactory.getInstance().getRootPathVideos(dn);
	}

	final File file = new File(filepath, LAST_ID_FILENAME);
	// TODO nicht immer lesend auf datei zugreifen! in variable cachen
	final String content = FileReader.getContent(file, DEFAULT_ENCODING).trim();
	result = Long.parseLong(content) + 1;
	FileWriter.writeContent(file, String.valueOf(result), DEFAULT_ENCODING);
	return result;
    }

    public long getLastId(final DomainName dn, final Class cls) {
	long result = 0;
	String filepath = null;
	if (cls == Article.class) {
	    filepath = FilePathFactory.getInstance().getRootPathArticles(dn);
	} else if (cls == Author.class) {
	    filepath = FilePathFactory.getInstance().getRootPathAuthors(dn);
	} else if (cls == Category.class) {
	    filepath = FilePathFactory.getInstance().getRootPathCategories(dn);
	} else if (cls == Image.class) {
	    filepath = FilePathFactory.getInstance().getRootPathImages(dn);
	} else if (cls == Text.class) {
	    filepath = FilePathFactory.getInstance().getRootPathTexts(dn);
	} else if (cls == Video.class) {
	    filepath = FilePathFactory.getInstance().getRootPathVideos(dn);
	}

	final File file = new File(filepath, LAST_ID_FILENAME);
	// TODO nicht immer lesend auf datei zugreifen! in variable cachen
	final String content = FileReader.getContent(file, DEFAULT_ENCODING).trim();
	result = Long.parseLong(content);
	return result;
    }

    private void delete(final DomainName dn, final Article article) {
	// delete {id}.html
	String filepath = FilePathFactory.getInstance().getFilePath(dn, article);
	File file = new File(filepath);
	if (file.exists()) {
	    file.delete();
	}

	// delete {id}.xml
	filepath = FilePathFactory.getInstance().getMetaFilePath(dn, article);
	file = new File(filepath);
	if (file.exists()) {
	    file.delete();
	}

	// delete from cache
	final String key = String.valueOf(article.getId());
	ARTICLES.remove(getDomainNamePrefix(dn) + key);
    }

    private void delete(final DomainName dn, final Category category) {
	// delete {id}.xml
	final String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, category);
	final File file = new File(filepath);
	if (file.exists()) {
	    file.delete();
	}

	// delete from cache
	final String key = String.valueOf(category.getId());
	CATEGORIES.remove(getDomainNamePrefix(dn) + key);
    }

    private void delete(final DomainName dn, final Image image) {
	// delete {id}.xml
	String filepath = FilePathFactory.getInstance().getMetaFilePath(dn, image);
	delete(filepath);

	// delete binary data files (jpg, ...)
	filepath = FilePathFactory.getInstance().getFilePath(dn, image);
	delete(filepath);
	filepath = FilePathFactory.getInstance().getPreviewFilePath(dn, image);
	delete(filepath);
	filepath = FilePathFactory.getInstance().getThumbnailFilePath(dn, image);
	delete(filepath);

	// delete from cache
	final String key = String.valueOf(image.getId());
	IMAGES.remove(getDomainNamePrefix(dn) + key);
    }

    private void delete(final String filePath) {
	final File file = new File(filePath);
	if (file.exists()) {
	    file.delete();
	}
    }

    public File getPreviewFile(final DomainName dn, final Object obj) {
	if (obj instanceof Image) {
	    final Image img = (Image) obj;
	    final String filepath = FilePathFactory.getInstance().getPreviewFilePath(dn, img);
	    return new File(filepath);
	}

	return null;
    }

    public File getThumbnailFile(final DomainName dn, final Object obj) {
	if (obj instanceof Image) {
	    final Image img = (Image) obj;
	    final String filepath = FilePathFactory.getInstance().getThumbnailFilePath(dn, img);
	    if (filepath == null) {
		return null;
	    }
	    return new File(filepath);
	}

	return null;
    }

    public void setUseCache(final boolean useCache) {
	this.useCache = useCache;

    }

    public boolean useCache() {
	return useCache;
    }

    public List<Category> getRootCategories(final DomainName dn) {
	List<Category> result = null;

	if (useCache()) {
	    result = (List<Category>) ROOT_CATEGORIES.get(getDomainNamePrefix(dn));
	}
	if (result == null || result.isEmpty()) {
	    final RootCategoriesPersistenceManager pm = new FileRootCategoriesPersistenceManager();
	    result = pm.load(dn);
	    if (result != null) {
		ROOT_CATEGORIES.put(getDomainNamePrefix(dn), result);
	    }
	}
	return result;
    }

    public void saveRootCategories(final DomainName dn, final List<Category> rootCategories) {
	final RootCategoriesPersistenceManager pm = new FileRootCategoriesPersistenceManager();
	pm.save(dn, rootCategories);
	ROOT_CATEGORIES.put(getDomainNamePrefix(dn), rootCategories);
    }

    public List<Category> getSiteCategories(final DomainName dn) {
	List<Category> result = null;

	if (useCache()) {
	    result = (List<Category>) SITE_CATEGORIES.get(getDomainNamePrefix(dn));
	}
	if (result == null || result.isEmpty()) {
	    final SiteCategoriesPersistenceManager pm = new FileSiteCategoriesPersistenceManager();
	    result = pm.load(dn);
	    if (result != null) {
		SITE_CATEGORIES.put(getDomainNamePrefix(dn), result);
	    }
	}
	return result;
    }

    public void saveSiteCategories(final DomainName dn, final List<Category> categories) {
	final SiteCategoriesPersistenceManager pm = new FileSiteCategoriesPersistenceManager();
	pm.save(dn, categories);
	SITE_CATEGORIES.put(getDomainNamePrefix(dn), categories);
    }
}

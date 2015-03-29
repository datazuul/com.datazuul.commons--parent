package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;

public abstract class ImagePersistenceManager {
    public void save(final DomainName dn, final Image image) {
	saveOriginal(dn, image);
	savePreview(dn, image);
	saveThumbnail(dn, image);
	saveMetadata(dn, image);
    }

    public Image load(final DomainName dn, final Image image) {
	final Image result = loadMetadata(dn, image);
	if (result != null) {
	    // TODO: read lazy anywhere else? (performance, memory...)
	    loadThumbnail(dn, result);
	    loadPreview(dn, result);
	    loadOriginal(dn, result);
	}
	return result;
    }

    protected abstract void loadOriginal(DomainName dn, Image image);

    protected abstract void loadPreview(DomainName dn, Image image);

    protected abstract void loadThumbnail(DomainName dn, Image image);

    protected abstract Image loadMetadata(DomainName dn, Image image);

    /**
     * Save image in original size.
     * 
     * @param image
     *            image to save
     */
    protected abstract void saveOriginal(DomainName dn, Image image);

    /**
     * Save image in preview (middle) size.
     * 
     * @param image
     *            image to save
     */
    protected abstract void savePreview(DomainName dn, Image image);

    /**
     * Save image in thumbnail (small) size.
     * 
     * @param image
     *            image to save
     */
    protected abstract void saveThumbnail(DomainName dn, Image image);

    /**
     * Save meta data.
     * 
     * @param image
     *            image to save
     */
    protected abstract void saveMetadata(DomainName dn, Image image);
}

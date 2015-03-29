package com.datazuul.commons.cms.business;

import com.datazuul.commons.cms.backend.NetCMSRepository;
import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Image;

/**
 * @author ralf
 */
public class ImageService {

    public void delete(final DomainName dn, final Image image) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	repository.deleteImage(dn, image);
	repository.unIndex(dn, image);
    }

    public Image load(final DomainName dn, final long id) {
	final NetCMSRepository repository = NetCMSRepository.getInstance();
	return repository.getImageById(dn, id);
    }
}

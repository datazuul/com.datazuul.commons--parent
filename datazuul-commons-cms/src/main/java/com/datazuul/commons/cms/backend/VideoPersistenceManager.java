package com.datazuul.commons.cms.backend;

import com.datazuul.commons.cms.domain.DomainName;
import com.datazuul.commons.cms.domain.Video;

public abstract class VideoPersistenceManager {
	public void save(DomainName dn, Video video) {
		saveBinary(dn, video);
		saveMetadata(dn, video);
	}

	public Video load(DomainName dn, Video video) {
		Video result = loadMetadata(dn, video);
		if (result != null) {
			loadBinary(dn, result);
		}
		return result;
	}

	protected abstract void loadBinary(DomainName dn, Video video);

	protected abstract Video loadMetadata(DomainName dn, Video video);

	/**
	 * Save binary content of video.
	 * 
	 * @param video
	 *            video to save
	 */
	protected abstract void saveBinary(DomainName dn, Video video);

	/**
	 * Save meta data.
	 * 
	 * @param video
	 *            video to save
	 */
	protected abstract void saveMetadata(DomainName dn, Video video);
}

package com.datazuul.commons.cms.backend.filesystem;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.datazuul.commons.cms.domain.DomainName;

public class FilePathFactoryTest {

    @Test
    public void testGetRootPathRepository() {
	final DomainName dn = new DomainName("www.alexandria.de");
	final String rootPathRepository = FilePathFactory.getInstance().getRootPathRepository(dn);
	assertTrue("/var/www/www.alexandria.de/repository/".equals(rootPathRepository));
    }

}

package com.datazuul.commons.iiif.presentation;

import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class IiifManifestParser extends AbstractManifestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(IiifManifestParser.class);

    private static final GraciousManifestParser graciousManifestParser = new GraciousManifestParser();

    private static final StrictManifestParser strictManifestParser = new StrictManifestParser();

    public static DigitalObject toDigitalObject(URI manifestUri, int thumbnailWidth) throws IOException, TechnicalException {
        InputStream is;
        DigitalObject digitalObject = null;
        try {
            is = getContentInputStream(manifestUri);
            digitalObject = strictManifestParser.toDigitalObject(is, thumbnailWidth);
        } catch (Exception e) {
            is = getContentInputStream(manifestUri);
            digitalObject = graciousManifestParser.toDigitalObject(is, manifestUri, thumbnailWidth);
        }
        return digitalObject;
    }

    public DigitalObject toDigitalObject(Manifest manifest, int thumbnailWidth) throws TechnicalException, MalformedURLException {
        DigitalObject digitalObject = strictManifestParser.toDigitalObject(manifest, thumbnailWidth);
        return digitalObject;
    }
}

package com.datazuul.commons.iiif.presentation;

import static org.junit.jupiter.api.Assertions.*;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class StrictManifestParserTest {

    private static StrictManifestParser strictManifestParser;

    @BeforeAll
    static void beforeAll() {
        strictManifestParser = new StrictManifestParser();
    }

    @Test
    void toDigitalObject() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("manifests/archive-bub_gb_tLxEAAAAIAAJ-manifest.json");
        DigitalObject digitalObject = strictManifestParser.toDigitalObject(is, 250);

        String expectedPreviewImageUri = "https://ia802902.us.archive.org/BookReader/BookReaderPreview.php?id=bub_gb_tLxEAAAAIAAJ&subPrefix=bub_gb_tLxEAAAAIAAJ&itemPath=/14/items/bub_gb_tLxEAAAAIAAJ&server=ia802902.us.archive.org&page=preview&";
        String actualPreviewImageUri = digitalObject.getPreviewImage().getUri().toString();
        assertEquals(expectedPreviewImageUri, actualPreviewImageUri);

        String expectedLabel = "Acht Vorträge aus der Gesundheitslehre";
        String actualLabel = digitalObject.getLabel().getText();
        assertEquals(expectedLabel, actualLabel);
    }

    @Test
    void getThumbnail() {
        // TODO
    }
}
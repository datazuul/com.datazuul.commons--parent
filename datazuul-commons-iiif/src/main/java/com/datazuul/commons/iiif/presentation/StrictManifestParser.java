package com.datazuul.commons.iiif.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.model.exception.http.HttpException;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.text.LocalizedStructuredContent;
import de.digitalcollections.model.text.LocalizedText;
import de.digitalcollections.model.text.StructuredContent;
import de.digitalcollections.model.text.contentblock.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StrictManifestParser extends AbstractManifestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrictManifestParser.class);

    private final ObjectMapper objectMapper = new IiifObjectMapper();

    public DigitalObject toDigitalObject(final InputStream is, int thumbnailWidth) throws IOException, HttpException {
        this.thumbnailWidth = thumbnailWidth;
        Manifest manifest = objectMapper.readValue(is, Manifest.class);
        return toDigitalObject(manifest, thumbnailWidth);
    }

    private LocalizedText convertPropertyValueToLocalizedText(final PropertyValue propertyValue) {
        if (propertyValue != null) {
            LocalizedText result = new LocalizedText();
            Set<Locale> localizations = propertyValue.getLocalizations();
            for (Locale localization : localizations) {
                String value = propertyValue.getFirstValue(localization);
                result.put(localization, value);
            }
            return result;
        }
        return null;
    }

    private LocalizedStructuredContent convertPropertyValueToLocalizedStructuredContent(final PropertyValue propertyValue) {
        if (propertyValue != null) {
            LocalizedStructuredContent result = new LocalizedStructuredContent();
            Set<Locale> localizations = propertyValue.getLocalizations();
            for (Locale localization : localizations) {
                String value = propertyValue.getFirstValue(localization);
                StructuredContent structuredContent = new StructuredContent();
                // TODO parse text and create adaequate content blocks?
                structuredContent.addContentBlock(new Paragraph(value));
                result.put(localization, structuredContent);
            }
            return result;
        }
        return null;
    }

    /**
     * Thumbnail: "A small image that depicts or pictorially represents the resource that the property is attached to, such as the title page,
     * a significant image or rendering of a canvas with multiple content resources associated with it.
     * It is recommended that a IIIF Image API service be available for this image for manipulations such as resizing.
     * If a resource has multiple thumbnails, then each of them should be different."
     * <p>
     * see http://iiif.io/api/presentation/2.1/#thumbnail
     *
     * @param manifest iiif manifest
     * @return thumbnail representing this manifest's object
     */
    public Thumbnail getThumbnail(Manifest manifest) {
    /*
    A manifest should have exactly one thumbnail image, and may have more than one.
    A sequence may have one or more thumbnails and should have at least one thumbnail if there are multiple sequences in a single manifest.
    A canvas may have one or more thumbnails and should have at least one thumbnail if there are multiple images or resources that make up the representation.
    A content resource may have one or more thumbnails and should have at least one thumbnail if it is an option in a choice of resources.
    Other resource types may have one or more thumbnails.
     */
        ImageContent imageContent = null;

        // A manifest should have exactly one thumbnail image, and may have more than one.
        if (manifest.getThumbnails() != null) {
            imageContent = manifest.getThumbnail();
        }

        if (imageContent == null && manifest.getDefaultSequence() != null) {
            // A sequence may have one or more thumbnails and should have at least one thumbnail if there are multiple sequences in a single manifest.
            imageContent = manifest.getDefaultSequence().getThumbnail();
        }

        if (imageContent == null && manifest.getDefaultSequence() != null && manifest.getDefaultSequence().getCanvases() != null) {
            // A canvas may have one or more thumbnails and should have at least one thumbnail if there are multiple images or resources that make up the representation.
            imageContent = manifest.getDefaultSequence().getCanvases().stream()
                    .map(c -> c.getThumbnails())
                    .filter(ts -> ts != null && ts.size() > 0)
                    .map(ts -> ts.get(0))
                    .findFirst().orElse(null);

        }

        if (imageContent == null) {
            // No thumbnail found, yet. Take the first image of first canvas as "thumbnail".
            imageContent = manifest.getDefaultSequence().getCanvases().get(0).getImages().stream()
                    .map(Annotation::getResource)
                    .filter(ImageContent.class::isInstance)
                    .map(ImageContent.class::cast)
                    .findFirst().orElse(null);
        }

        if (imageContent != null) {
            // thumbnail candidate found
            ImageService imageService = null;
            if (imageContent.getServices() != null) {
                imageService = imageContent.getServices().stream()
                        .filter(ImageService.class::isInstance)
                        .map(ImageService.class::cast)
                        .findFirst().orElse(null);
            }
            if (imageService != null) {
                boolean isV1 = imageService.getProfiles().stream()
                        .map(p -> p.getIdentifier().toString())
                        .anyMatch(ImageApiProfile.V1_PROFILES::contains);

                String serviceUrl = imageService.getIdentifier().toString();
                if (serviceUrl.endsWith("/")) {
                    serviceUrl = serviceUrl.substring(0, serviceUrl.length() - 1);
                }

                List<Size> sizes = imageService.getSizes();
                return createThumbnail(sizes, serviceUrl, isV1);
            } else {
                return new Thumbnail(imageContent.getIdentifier().toString());
            }
        }
        return null;
    }

    public DigitalObject toDigitalObject(Manifest manifest, int thumbnailWidth) throws MalformedURLException {
        DigitalObject digitalObject = new DigitalObject();
        // label
        final PropertyValue propertyValueLabel = manifest.getLabel();
        LocalizedText label = convertPropertyValueToLocalizedText(propertyValueLabel);
        digitalObject.setLabel(label);

        // description
        final PropertyValue propertyValueDescription = manifest.getDescription();
        LocalizedStructuredContent description = convertPropertyValueToLocalizedStructuredContent(propertyValueDescription);
        digitalObject.setDescription(description);

        // previewImage
        Thumbnail thumbnail = getThumbnail(manifest);
        setPreviewImage(thumbnail, digitalObject);

        // manifest file
        addManifestFile(manifest.getIdentifier(), digitalObject);

        // TODO add attribution, license, navDate, logo, related, seeAlso
        return digitalObject;
    }
}

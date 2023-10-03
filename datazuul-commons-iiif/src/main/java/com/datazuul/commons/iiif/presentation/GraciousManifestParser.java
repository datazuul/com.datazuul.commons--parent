package com.datazuul.commons.iiif.presentation;

import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.text.LocalizedStructuredContent;
import de.digitalcollections.model.text.LocalizedText;
import de.digitalcollections.model.text.StructuredContent;
import de.digitalcollections.model.text.contentblock.Paragraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GraciousManifestParser extends AbstractManifestParser {
    public static final Locale DEFAULT_LOCALE = Locale.GERMAN;
    private static final Logger LOGGER = LoggerFactory.getLogger(GraciousManifestParser.class);

    private LocalizedStructuredContent convertHashMapToLocalizedStructuredContent(final HashMap<Locale, String> localizedText) {
        if (localizedText != null) {
            LocalizedStructuredContent result = new LocalizedStructuredContent();
            Set<Locale> localizations = localizedText.keySet();
            for (Locale localization : localizations) {
                String value = localizedText.get(localization);
                StructuredContent structuredContent = new StructuredContent();
                // TODO parse text and create adequate content blocks?
                structuredContent.addContentBlock(new Paragraph(value));
                result.put(localization, structuredContent);
            }
            return result;
        }
        return null;
    }

    public LocalizedText convertHashMapToLocalizedText(HashMap<Locale, String> localizedText) {
        if (localizedText != null) {
            LocalizedText result = new LocalizedText();
            Set<Locale> localizations = localizedText.keySet();
            for (Locale localization : localizations) {
                String value = localizedText.get(localization);
                result.put(localization, value);
            }
            return result;
        }
        return null;
    }

    /**
     * Language may be associated with strings that are intended to be displayed to the user with the following pattern of
     *
     * @value plus the RFC 5646 code in
     */
    private void fillFromJsonObject(JSONObject jsonObject, DigitalObject digitalObject, URI manifestUri) {
        // label
        Object labelObject = jsonObject.get("label");
        HashMap<Locale, String> localizedLabels = getLocalizedStrings(labelObject);
        LocalizedText label = convertHashMapToLocalizedText(localizedLabels);
        digitalObject.setLabel(label);

        // description
        Object descriptionObject = jsonObject.get("description");
        HashMap<Locale, String> localizedDescriptions = getLocalizedStrings(descriptionObject);
        LocalizedStructuredContent description = convertHashMapToLocalizedStructuredContent(localizedDescriptions);
        digitalObject.setDescription(description);

        // previewImage
        Thumbnail thumbnail = getThumbnail(jsonObject);
        try {
            setPreviewImage(thumbnail, digitalObject);
        } catch (MalformedURLException ex) {
            LOGGER.warn("Could not set preview image for IIIF manifest " + manifestUri);
        }

        // manifest file
        String id = (String) jsonObject.get("@id");
        addManifestFile(URI.create(id), digitalObject);
    }

    public HashMap<Locale, String> getLocalizedStrings(Object jsonNode) {
        HashMap<Locale, String> result = new HashMap<>();
        if (jsonNode == null) {
            return result;
        }
        if (JSONArray.class.isAssignableFrom(jsonNode.getClass())) {
            JSONArray descriptions = (JSONArray) jsonNode;
            for (Object descr : descriptions) {
                JSONObject descrObj = (JSONObject) descr;
                String value = (String) descrObj.get("@value");
                String language = (String) descrObj.get("@language");
                result.put(new Locale(language), value);
            }
        } else {
            String value = (String) jsonNode;
            result.put(DEFAULT_LOCALE, value);
        }
        return result;
    }

    private Thumbnail getThumbnail(JSONObject manifestObj) {
        JSONObject thumbnailObj;

        // A manifest should have exactly one thumbnail image, and may have more than one.
        thumbnailObj = (JSONObject) manifestObj.get("thumbnail");

        JSONObject firstSequence = null;
        if (thumbnailObj == null) {
            // A sequence may have one or more thumbnails and should have at least one thumbnail if there are multiple sequences in a single manifest.
            JSONArray sequencesArray = (JSONArray) manifestObj.get("sequences");
            if (sequencesArray != null) {
                firstSequence = (JSONObject) sequencesArray.get(0);
                if (firstSequence != null) {
                    thumbnailObj = (JSONObject) firstSequence.get("thumbnail");
                }
            }
        }

        JSONArray canvases = null;
        if (thumbnailObj == null && firstSequence != null) {
            // A canvas may have one or more thumbnails and should have at least one thumbnail if there are multiple images or resources that make up the representation.
            canvases = (JSONArray) firstSequence.get("canvases");
            if (canvases != null) {
                Object obj = canvases.stream()
                        .map(c -> ((JSONObject) c).get("thumbnail"))
                        .filter(Objects::nonNull)
                        .findFirst().orElse(null);
                if (obj != null) {
                    if (obj instanceof JSONObject) {
                        thumbnailObj = (JSONObject) obj;
                    }
                    // TODO String = url
                }
            }
        }

        if (thumbnailObj == null && canvases != null) {
            // No thumbnail found, yet. Take the first image of first canvas as "thumbnail".
            JSONObject firstCanvas = ((JSONObject) canvases.get(0));
            if (firstCanvas != null) {
                JSONArray images = (JSONArray) firstCanvas.get("images");
                if (images != null) {
                    Object obj = images.stream()
                            .map(i -> ((JSONObject) i).get("resource"))
                            .findFirst().orElse(null);
                    if (obj != null) {
                        if (obj instanceof JSONObject) {
                            thumbnailObj = (JSONObject) obj;
                        }
                        // TODO String = url
                    }
                }
            }
        }

        if (thumbnailObj != null) {
            // thumbnail candidate found
            JSONObject serviceObj = (JSONObject) thumbnailObj.get("service");
            if (serviceObj != null) {
                boolean isV1 = false;

                String profile = null;
                Object profileObj = serviceObj.get("profile");
                if (profileObj == null) {
                    profileObj = serviceObj.get("dcterms:conformsTo");
                }
                if (profileObj instanceof JSONArray) {
                    profile = (String) ((JSONArray) profileObj).get(0);
                }
                if (profileObj instanceof String) {
                    profile = (String) profileObj;
                }
                if (profile != null) {
                    isV1 = ImageApiProfile.V1_PROFILES.contains(profile);
                }

                String serviceUrl = (String) serviceObj.get("@id");
                if (serviceUrl.endsWith("/")) {
                    serviceUrl = serviceUrl.substring(0, serviceUrl.length() - 1);
                }

                List<Size> sizes = new ArrayList<>();
                JSONArray sizesArray = (JSONArray) serviceObj.get("sizes");
                if (sizesArray != null) {
                    for (Object size : sizesArray) {
                        JSONObject sizeObj = (JSONObject) size;
                        int width = ((Long) sizeObj.get("width")).intValue();
                        int height = ((Long) sizeObj.get("height")).intValue();
                        sizes.add(new Size(width, height));
                    }
                }
                return createThumbnail(sizes, serviceUrl, isV1);
            } else {
                return new Thumbnail(thumbnailObj.get("@id").toString());
            }
        }
        return null;
    }

    public DigitalObject toDigitalObject(final InputStream is, URI manifestUri, int thumbnailWidth) throws IOException {
        this.thumbnailWidth = thumbnailWidth;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

            DigitalObject digitalObject = new DigitalObject();
            fillFromJsonObject(jsonObject, digitalObject, manifestUri);
            return digitalObject;
        } catch (ParseException ex) {
            LOGGER.warn("Could not parse json at {}.", manifestUri);
            throw new IOException("Invalid JSON.");
        }
    }
}

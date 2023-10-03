package com.datazuul.commons.iiif.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.iiif.model.Service;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.model.exception.http.HttpException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.ApplicationFileResource;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractManifestParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManifestParser.class);

  private final ObjectMapper objectMapper = new IiifObjectMapper();

  protected int thumbnailWidth = 200;

  protected void addManifestFile(URI manifestUri, DigitalObject digitalObject) throws HttpException {
    // manifest file
    String encodedManifestUri = new String(Base64.getUrlEncoder().encode(manifestUri.toString().getBytes()));
    //    String encodedManifestUri = URLEncoder.encode(manifestUri.toString(), StandardCharsets.UTF_8);
    FileResource manifestFileResource = null;
    if (manifestFileResource == null) {
      manifestFileResource = new ApplicationFileResource();
      // String manifestUri = manifest.getIdentifier().toString();
      manifestFileResource.setUri(manifestUri);
      manifestFileResource.addIdentifier(Identifier.builder().namespace("uri-base64").id(encodedManifestUri).build());
      manifestFileResource.setFilename("manifest.json");
      manifestFileResource.setMimeType(MimeType.fromFilename("manifest.json"));
      manifestFileResource.setLabel(digitalObject.getLabel());
    }
    
    digitalObject.getFileResources().add(manifestFileResource);
  }

  protected Thumbnail createThumbnail(List<Size> sizes, String serviceUrl, boolean isV1) {
    if (sizes == null || sizes.isEmpty()) {
      try {
        InputStream infoJsonIS = getContentInputStream(URI.create(serviceUrl + "/info.json"));
        // get info.json for available sizes
        ImageService imageServiceExternal = (ImageService) objectMapper.readValue(infoJsonIS, Service.class);
        sizes = imageServiceExternal.getSizes();
      } catch (IOException ex) {
        LOGGER.debug("Can not read info.json", ex);
      }
    }
    int bestWidth = thumbnailWidth;
    if (sizes != null) {
      bestWidth = sizes.stream()
              .filter(s -> s.getWidth() >= thumbnailWidth)
              .sorted(Comparator.comparing(s -> Math.abs(thumbnailWidth - s.getWidth())))
              .map(Size::getWidth).findFirst().orElse(thumbnailWidth);
    }
    // TODO add check, if minimal width is met (make minWidth configurable), otherwise get second best width...
    String thumbnailUrl = String.format("%s/full/%d,/0/", serviceUrl, bestWidth);
    if (isV1) {
      thumbnailUrl += "native.jpg";
    } else {
      thumbnailUrl += "default.jpg";
    }
    // FIXME causes DoS-protection
//    if (thumbnailUrl.startsWith("http")) {
//      // try to get thumbnail url 200 response for head request
//      HttpClient httpClient = HttpClientBuilder.create().build();
//      HttpHead httpHead = new HttpHead(thumbnailUrl);
//      try {
//        HttpResponse response = httpClient.execute(httpHead);
//        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//          return null; // no valid thumbnail available
//        }
//      } catch (IOException ex) {
//        return null; // no valid thumbnail available
//      }
//    }
    LOGGER.debug("Thumbnail url = '{}'", thumbnailUrl);
    final Thumbnail thumbnail = new Thumbnail(thumbnailUrl);
    if (serviceUrl != null) {
      thumbnail.setIiifImageServiceUri(serviceUrl);
    }
    return thumbnail;
  }

  public InputStream getContentInputStream(URI uri) throws UnsupportedOperationException, IOException {
    try {
      HttpClient client = HttpClient.newHttpClient();
      client.followRedirects();

      HttpRequest request = HttpRequest.newBuilder()
              .version(Version.HTTP_2)
              .GET()
              .timeout(Duration.ofSeconds(30))
              .uri(uri)
              //              .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
              //              .setHeader("Accept-Encoding","gzip, deflate, br")
              //              .setHeader("Accept-Language","de,en-US;q=0.9,en;q=0.8")
              //              .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
              //              .setHeader("Cookie","_ga=GA1.2.689528026.1609269209; _gid=GA1.2.1433597648.1609269209")
              .build();

      HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
      final InputStream content = response.body();
      return content;
    } catch (IOException | InterruptedException ex) {
      LOGGER.error("Error getting input stream", ex);
    }
    return null;
  }

  private MimeType getMimeType(Thumbnail thumbnail) {
    HttpClient client = HttpClient.newHttpClient();
    client.followRedirects();

    HttpRequest request = HttpRequest.newBuilder()
            .version(Version.HTTP_2)
            .GET()
            .timeout(Duration.ofSeconds(30))
            .uri(URI.create(thumbnail.getUrl()))
            .build();

    HttpResponse<Void> response;
    try {
      response = client.send(request, BodyHandlers.discarding());
      final HttpHeaders headers = response.headers();
      Optional<String> contentType = headers.firstValue("Content-Type");
      if (contentType.isPresent()) {
        String mimeType = contentType.get().split(";")[0].trim();
        return MimeType.fromTypename(mimeType);
      }
    } catch (IOException | InterruptedException ex) {
      LOGGER.error("Error getting mimetype", ex);
    }
    return null; // no valid thumbnail available
  }

  public void setPreviewImage(Thumbnail thumbnail, DigitalObject digitalObject) throws MalformedURLException, HttpException {
    if (thumbnail != null) {
      String thumbnailUrl = thumbnail.getUrl();
//      MimeType mimeType = getMimeType(thumbnail); // FIXME causes DoS-Protection - slowdown...
      String encodedThumbnailurl = new String(Base64.getUrlEncoder().encode(thumbnailUrl.getBytes()));
//      String encodedThumbnailurl = URLEncoder.encode(thumbnailUrl, StandardCharsets.UTF_8);
//      encodedThumbnailurl = encodedThumbnailurl.replaceAll("\\.", "%2E");
      FileResource thumbnailFile = null;
      if (thumbnailFile == null) {
        thumbnailFile = new ImageFileResource();
        final URI uri = URI.create(thumbnailUrl);
        thumbnailFile.setUri(uri);
        thumbnailFile.addIdentifier(Identifier.builder().namespace("uri-base64").id(encodedThumbnailurl).build());
        final String filename = FilenameUtils.getName(uri.toURL().getFile());
        thumbnailFile.setFilename(filename);
        MimeType mimetype = MimeType.fromFilename(filename);
        if (mimetype == null) {
          mimetype = MimeType.MIME_IMAGE;
        }
        thumbnailFile.setMimeType(mimetype);
      }
      ImageFileResource previewImage = new ImageFileResource();
      previewImage.setUuid(thumbnailFile.getUuid());
      previewImage.setUri(thumbnailFile.getUri());
      previewImage.setIdentifiers(thumbnailFile.getIdentifiers());
      final String filename = thumbnailFile.getFilename();
      previewImage.setFilename(filename);
      MimeType mimetype = MimeType.fromFilename(filename);
      if (mimetype == null) {
        mimetype = MimeType.MIME_IMAGE;
      }
      previewImage.setMimeType(mimetype);
      digitalObject.setPreviewImage(previewImage);
    }
  }

}

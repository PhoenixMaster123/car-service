package springboot.bg.harisauto.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileStorageService.java - Service class for handling file uploads locally.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class FileStorageService {

  private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
  private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB

  private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
      // SVG is deliberately excluded: it is an XML document that can carry script,
      // and /uploads/** is served from this application's own origin.
      "image/jpeg", "image/png", "image/gif", "image/webp"
  );
  private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
      "video/mp4", "video/webm", "video/ogg", "video/quicktime", "video/x-msvideo"
  );

  private final Path fileStorageLocation;

  public FileStorageService() {
    // Store uploads outside classpath so they persist across rebuilds
    this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
      log.info("File storage initialized at: {}", this.fileStorageLocation);
    } catch (Exception ex) {
      log.error("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  /**
   * Stores a file in the local file system.
   *
   * @param file the MultipartFile to save.
   * @param subDir the subdirectory within 'uploads/'.
   * @return the relative URL path to access the file (e.g., /uploads/news/filename.jpg)
   */
  public String storeFile(MultipartFile file, String subDir) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    String contentType = file.getContentType();
    if (contentType == null) {
      log.warn("File has no content type, rejecting upload");
      return null;
    }

    // Validate file type and size
    if (contentType.startsWith("image/")) {
      if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
        log.warn("Unsupported image type: {}", contentType);
        return null;
      }
      if (!hasKnownImageSignature(file)) {
        log.warn("Rejected upload: content does not match the declared type {}", contentType);
        return null;
      }
      if (file.getSize() > MAX_IMAGE_SIZE) {
        log.warn("Image too large: {} bytes (max {} bytes)", file.getSize(), MAX_IMAGE_SIZE);
        return null;
      }
    } else if (contentType.startsWith("video/")) {
      if (!ALLOWED_VIDEO_TYPES.contains(contentType)) {
        log.warn("Unsupported video type: {}", contentType);
        return null;
      }
      if (file.getSize() > MAX_VIDEO_SIZE) {
        log.warn("Video too large: {} bytes (max {} bytes)", file.getSize(), MAX_VIDEO_SIZE);
        return null;
      }
    } else {
      log.warn("Unsupported file type: {}", contentType);
      return null;
    }

    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if (originalFileName.contains("..")) {
        log.warn("Rejected upload: file name contains a path sequence");
        return null;
      }

      String fileExtension = "";
      int i = originalFileName.lastIndexOf('.');
      if (i > 0) {
        fileExtension = originalFileName.substring(i);
      }
      
      String generatedFileName = UUID.randomUUID().toString() + fileExtension;
      
      Path targetLocation = this.fileStorageLocation.resolve(subDir).normalize();
      if (!targetLocation.startsWith(this.fileStorageLocation)) {
        log.warn("Rejected upload: subdirectory {} escapes the storage root", subDir);
        return null;
      }
      Files.createDirectories(targetLocation);
      
      Path filePath = targetLocation.resolve(generatedFileName);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      log.info("File stored successfully: {}", filePath);
      return "/uploads/" + subDir + "/" + generatedFileName;
    } catch (IOException ex) {
      log.error("Could not store file " + originalFileName + ". Please try again!", ex);
      return null;
    }
  }

  /**
   * Checks the leading bytes of an upload against the signatures of the image formats
   * we accept. The declared {@code Content-Type} comes from the client and can be set to
   * anything, so it is not trusted on its own.
   *
   * @param file The uploaded file.
   * @return true if the content looks like a JPEG, PNG, GIF or WebP.
   */
  private boolean hasKnownImageSignature(MultipartFile file) {
    byte[] header = new byte[12];
    try (InputStream in = file.getInputStream()) {
      if (in.readNBytes(header, 0, header.length) < header.length) {
        return false;
      }
    } catch (IOException ex) {
      log.warn("Could not read upload header", ex);
      return false;
    }
    return matches(header, 0, 0xFF, 0xD8, 0xFF)                                  // JPEG
        || matches(header, 0, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)    // PNG
        || matches(header, 0, 0x47, 0x49, 0x46, 0x38)                            // GIF87a/89a
        || (matches(header, 0, 0x52, 0x49, 0x46, 0x46)
            && matches(header, 8, 0x57, 0x45, 0x42, 0x50));                      // RIFF....WEBP
  }

  /**
   * Compares a byte sequence at the given offset.
   *
   * @param data The bytes to inspect.
   * @param offset Where the signature should start.
   * @param signature The expected unsigned byte values.
   * @return true if every byte matches.
   */
  private boolean matches(byte[] data, int offset, int... signature) {
    for (int i = 0; i < signature.length; i++) {
      if ((data[offset + i] & 0xFF) != signature[i]) {
        return false;
      }
    }
    return true;
  }
}

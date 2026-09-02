package springboot.bg.harisauto.common.service;

import java.io.IOException;
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
      "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
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
        throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
      }

      String fileExtension = "";
      int i = originalFileName.lastIndexOf('.');
      if (i > 0) {
        fileExtension = originalFileName.substring(i);
      }
      
      String generatedFileName = UUID.randomUUID().toString() + fileExtension;
      
      Path targetLocation = this.fileStorageLocation.resolve(subDir).normalize();
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
}

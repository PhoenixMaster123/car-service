package springboot.bg.harisauto.news.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springboot.bg.harisauto.common.service.FileStorageService;
import springboot.bg.harisauto.news.model.News;
import springboot.bg.harisauto.news.repository.NewsRepository;

/**
 * NewsService.java - Handles business logic for News articles.
 *
 * @author Kristian Popov
 */
@Service
@RequiredArgsConstructor
public class NewsService {

  private final NewsRepository newsRepository;
  private final FileStorageService fileStorageService;

  public List<News> getAllNews() {
    return newsRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
  }

  public News createNews(String title, String content, String author, MultipartFile image, MultipartFile video) {
    String imageUrl = store(image, "image");
    String videoUrl = store(video, "video");

    News news = News.builder()
        .title(title)
        .content(content)
        .author(author)
        .imageUrl(imageUrl)
        .videoUrl(videoUrl)
        .dateCreated(LocalDateTime.now())
        .build();

    return newsRepository.save(news);
  }

  public void deleteNews(Long id) {
    newsRepository.deleteById(id);
  }

  /**
   * Stores an optional upload, failing loudly when the file was rejected.
   *
   * <p>{@code storeFile} returns null both for "nothing was uploaded" and for
   * "the upload was rejected". Without this distinction the article would be saved
   * with no media while the admin is told it succeeded.</p>
   *
   * @param file The uploaded file, may be null or empty.
   * @param kind Label used in the error message.
   * @return The stored URL, or null if no file was supplied.
   */
  private String store(MultipartFile file, String kind) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    String url = fileStorageService.storeFile(file, "news");
    if (url == null) {
      throw new IllegalArgumentException(
          "The " + kind + " could not be stored. Check that it is a supported format "
          + "and within the size limit.");
    }
    return url;
  }
}

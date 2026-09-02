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
    String imageUrl = null;
    if (image != null && !image.isEmpty()) {
      imageUrl = fileStorageService.storeFile(image, "news");
    }

    String videoUrl = null;
    if (video != null && !video.isEmpty()) {
      videoUrl = fileStorageService.storeFile(video, "news");
    }

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
}

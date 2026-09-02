package springboot.bg.harisauto.news.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.news.model.News;
import springboot.bg.harisauto.news.service.NewsService;

/**
 * AdminNewsController.java - Handles admin interactions for News management.
 *
 * @author AI Engine
 */
@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

  private final NewsService newsService;

  @GetMapping
  public ModelAndView getNewsPage() {
    ModelAndView modelAndView = new ModelAndView("account/admin/news/admin-news");
    List<News> allNews = newsService.getAllNews();
    modelAndView.addObject("newsList", allNews);
    return modelAndView;
  }

  @PostMapping("/create")
  public ModelAndView createNews(
      @RequestParam("title") String title,
      @RequestParam("content") String content,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam(value = "video", required = false) MultipartFile video,
      @AuthenticationPrincipal AuthenticationMetaData metaData,
      RedirectAttributes redirectAttributes) {

    try {
      // In a real scenario we could get the author name from metaData's User
      String author = metaData.getUsername();
      newsService.createNews(title, content, author, image, video);
      redirectAttributes.addFlashAttribute("success", "News article created successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error creating news: " + e.getMessage());
    }

    return new ModelAndView("redirect:/admin/news");
  }

  @PostMapping("/delete")
  public ModelAndView deleteNews(
      @RequestParam("id") Long id,
      RedirectAttributes redirectAttributes) {

    try {
      newsService.deleteNews(id);
      redirectAttributes.addFlashAttribute("success", "News article deleted.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error deleting news.");
    }
    
    return new ModelAndView("redirect:/admin/news");
  }
}

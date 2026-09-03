package springboot.bg.harisauto.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.news.service.NewsService;

/**
 * PagesController.java - Controller for handling static pages web requests.
 *
 * @author Kristian Popov
 */
@Controller
public class PageController {

  private final NewsService newsService;

  /** Constructor. */
  public PageController(NewsService newsService) {
    this.newsService = newsService;
  }

  /**
   * Shows the About Us page.
   *
   * @return The About Us page.
   */
  @GetMapping("/about")
  public ModelAndView showAboutUsPage() {
    return new ModelAndView("public/about-us");
  }

  /**
   * Shows the Careers page.
   *
   * @return The Careers page.
   */
  @GetMapping("/careers")
  public ModelAndView showCareersPage() {
    return new ModelAndView("public/careers");
  }

  /**
   * Shows the Locations page.
   *
   * @return The Locations page.
   */
  @GetMapping("/locations")
  public ModelAndView showLocationsPage() {
    return new ModelAndView("public/location");
  }

  /**
   * Shows the News page.
   *
   * @return The News page.
   */
  @GetMapping("/news")
  public ModelAndView showNewsPage() {
    ModelAndView modelAndView = new ModelAndView("public/news");
    modelAndView.addObject("newsList", newsService.getAllNews());
    return modelAndView;
  }
}
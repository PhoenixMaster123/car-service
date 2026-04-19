package springboot.bg.harisauto.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * PagesController.java - Controller for handling static pages web requests.
 *
 * @author Kristian Popov
 */
@Controller
public class PageController {

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
    return new ModelAndView("public/news");
  }
}
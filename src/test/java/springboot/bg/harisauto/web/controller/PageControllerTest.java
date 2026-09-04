package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import springboot.bg.harisauto.news.model.News;
import springboot.bg.harisauto.news.service.NewsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PageController.class)
public class PageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NewsService newsService;

  @Test
  @DisplayName("GET /about - Public Page - Should Return OK")
  void getRequestToAboutUs_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/about");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("public/about-us"));
  }

  @Test
  @DisplayName("GET /careers - Public Page - Should Return OK")
  void getRequestToCareers_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/careers");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("public/careers"));
  }

  @Test
  @DisplayName("GET /location - Public Page - Should Return OK")
  void getRequestToLocation_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/locations");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("public/location"));
    }

    @Test
    @DisplayName("GET /news - Public Page - Should Return OK")
    void getRequestToNews_ShouldReturnOk() throws Exception {

      MockHttpServletRequestBuilder request = get("/news");

      this.mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(view().name("public/news"));
    }

  @Test
  @DisplayName("GET /news - puts the stored articles on the model")
  void getRequestToNews_ShouldExposeNewsList() throws Exception {

    News article = News.builder()
        .id(1L)
        .title("New wash tunnel")
        .content("Now open.")
        .imageUrl("/uploads/news/abc.png")
        .videoUrl("/uploads/news/abc.mp4")
        .author("admin")
        .dateCreated(LocalDateTime.now())
        .build();
    when(newsService.getAllNews()).thenReturn(List.of(article));

    this.mockMvc.perform(get("/news"))
        .andExpect(status().isOk())
        .andExpect(view().name("public/news"))
        .andExpect(model().attribute("newsList", List.of(article)))
        // Assert on the rendered body, so the template itself is exercised: a bad
        // #temporals call or link expression would fail here rather than in production.
        .andExpect(content().string(containsString("New wash tunnel")))
        .andExpect(content().string(containsString("/uploads/news/abc.png")))
        .andExpect(content().string(containsString("/uploads/news/abc.mp4")));
  }
}

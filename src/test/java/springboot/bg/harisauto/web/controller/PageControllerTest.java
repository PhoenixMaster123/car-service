package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PageController.class)
public class PageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("GET /about - Public Page - Should Return OK")
  void getRequestToAboutUs_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/about");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("/public/about-us"));
  }

  @Test
  @DisplayName("GET /careers - Public Page - Should Return OK")
  void getRequestToCareers_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/careers");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("/public/careers"));
  }

  @Test
  @DisplayName("GET /location - Public Page - Should Return OK")
  void getRequestToLocation_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/locations");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("/public/location"));
    }

    @Test
    @DisplayName("GET /news - Public Page - Should Return OK")
    void getRequestToNews_ShouldReturnOk() throws Exception {

      MockHttpServletRequestBuilder request = get("/news");

      this.mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(view().name("/public/news"));
    }
}
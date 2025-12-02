package springboot.bg.harisauto.common.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.springframework.web.filter.GenericFilterBean;

/**
 * CustomFilter.java - A custom filter that passes the request and response
 *
 * @author Kristian Popov
 */
public class CustomFilter extends GenericFilterBean {

  /**
   * Processes the request and response.
   *
   * @param request  The request to process
   * @param response The response associated with the request
   * @param chain    The filter chain to pass the request and response to the next filter
   *
   * @throws IOException IOException
   * @throws ServletException ServletException
   */
  @Override
  public void doFilter(ServletRequest request,
      ServletResponse response, FilterChain chain) throws IOException, ServletException {
    chain.doFilter(request, response);
  }
}
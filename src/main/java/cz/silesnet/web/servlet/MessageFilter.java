package cz.silesnet.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Http request filert for handling messages communication between pages.
 *
 * @author Richard Sikora
 */
public class MessageFilter implements Filter {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  // ~ Methods
  // ----------------------------------------------------------------

  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse res,
                       FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    // FIXME can not be hardcoded use application globals
    // move success message from session and into request
    Object successMsg = request.getSession().getAttribute("successMsg");
    if (successMsg != null) {
      request.setAttribute("successMsg", successMsg);
      request.getSession().removeAttribute("successMsg");
    }
    // FIXME can not be hardcoded use application globals
    // move failure message from session and into request
    Object failureMsg = request.getSession().getAttribute("failureMsg");
    if (failureMsg != null) {
      request.setAttribute("failureMsg", failureMsg);
      request.getSession().removeAttribute("failureMsg");
    }
    // continue
    chain.doFilter(req, res);
  }

  public void init(FilterConfig arg0) throws ServletException {
  }
}
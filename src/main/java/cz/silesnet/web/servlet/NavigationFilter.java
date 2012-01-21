package cz.silesnet.web.servlet;

import cz.silesnet.util.NavigationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Stack;

/**
 * Filter supporting inteligent go back navigation. It keeps stack of visited
 * URLs that have been pushed by _navigationPushUrl parameter.
 *
 * @author Richard Sikora
 */
public class NavigationFilter implements Filter {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  // ~ Methods
  // ----------------------------------------------------------------

  public void destroy() {
    log.debug("Destroying...");
  }

  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest req, ServletResponse res,
                       FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    // get previous url from session
    String prevUrl = (String) WebUtils.getSessionAttribute(request,
        "_navPreviousUrl");

    // get url stack from session
    Stack<String> urlStack = (Stack<String>) WebUtils.getSessionAttribute(
        request, "_navUrlStack");

    // figure out clean url
    String urlQuery = request.getQueryString();
    String urlCleanQuery = NavigationUtils.removeSystemParameters(urlQuery);

    // cleanUrl should not be null acording to API (not said there)
    String cleanUrl = request.getServletPath();

    if (!GenericValidator.isBlankOrNull(urlCleanQuery))
      cleanUrl = cleanUrl + "?" + urlCleanQuery;

    log.debug("Session _navUrlStack: " + urlStack);

    // see if there is _navPushUrl parameter in request
    if ((urlQuery != null) && (urlQuery.contains("_navPushUrl"))) {
      // _navPushUrl present in request need to push previousUrl to stack

      // TODO if generated prevUrl == stored in session and there is _push
      // ommit pushing for double request
      if (cleanUrl.equals(prevUrl))
        // we have form repeating _navPushUrl
        log
            .debug("REPEATED _navPushUrl by some form controller dropping it!!!");
      else {
        // legal push, let's do it
        log
            .debug("_navPushUrl parameter found in request, so pushing previous url to stack...");

        if (urlStack == null) {
          // there is no url stack so create one for this session
          log.debug("Creating new stack for session _navUrlStack.");
          urlStack = new Stack<String>();
          WebUtils.setSessionAttribute(request, "_navUrlStack",
              urlStack);
        }

        // if previous url present in session push it to stack
        if (prevUrl != null) {
          urlStack.push(prevUrl);
          log.debug("_navPreviousUrl pushed to stack: " + urlStack);
        }
      }
    }

    // update previous url session attribute
    log.debug("Updating session _navPreviousUrl with: " + cleanUrl);
    // ommit printing links, koz to go back is used browsers back button
    if (!cleanUrl.contains("action=printBillTxt"))
      WebUtils.setSessionAttribute(request, "_navPreviousUrl", cleanUrl);

    // continue filtering
    chain.doFilter(req, res);
    // wrap request and response
    // chain.doFilter(new HttpRequestWrapper(request), new
    // HttpResponseWrapper((HttpServletResponse) res));
  }

  public void init(FilterConfig arg0) throws ServletException {
    log.debug("Initialising...");
  }
}
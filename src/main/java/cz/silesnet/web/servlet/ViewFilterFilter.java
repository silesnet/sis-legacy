package cz.silesnet.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter used to maintain session filterMap map.
 *
 * @author Richard Sikora
 */
public class ViewFilterFilter implements Filter {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  // ~ Methods
  // ----------------------------------------------------------------

  public void destroy() {
    log.debug("View Filter destroying....");
  }

  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest req, ServletResponse res,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;

    // get session attribute sisFilterMap
    Map<String, String> sisFilterMap = (Map<String, String>) request
        .getSession().getAttribute("sisFilterMap");

    // when attribute does not exist create one and bind it to session
    if (sisFilterMap == null) {
      log.debug("Creating new filterMap in session.");
      sisFilterMap = new HashMap<String, String>();
      // preset filtering customers only for active ones
      sisFilterMap.put("customer.billing.isActive", "true");
      sisFilterMap.put("wireless.active", "true");
      request.getSession().setAttribute("sisFilterMap", sisFilterMap);
    }

    // get parameters from request starting with _filter
    // Map<String, String> reqFilterParamsMap =
    // FilterUtils.getParamMap(request, "_filter");
    Map<String, String> reqFilterParamsMap = WebUtils
        .getParametersStartingWith(request, "_filter.");
    log.debug("Got filterParametersMap from current request: "
        + reqFilterParamsMap);

    if ((reqFilterParamsMap != null) && (reqFilterParamsMap.size() > 0)) {
      // we have some filter parameters in current request

      // iterate throught parameters and put em into session filter map
      for (String filterAttribute : reqFilterParamsMap.keySet()) {
        Object filterAttValue = reqFilterParamsMap.get(filterAttribute);
        log.debug("Intercepted filter param: " + filterAttribute
            + " with value :" + filterAttValue);

        String filterAttValueStr = "";

        if (filterAttValue instanceof String)
          filterAttValueStr = (String) filterAttValue;

        if ("".equals(filterAttValueStr)
            || "0".equals(filterAttValueStr)) {
          // remove filter attribute from filterMap
          log.debug("Removing filterAttribute from filterMap: "
              + filterAttribute);
          sisFilterMap.remove(filterAttribute);
        } else {
          // put filter attribute to filterMap
          log.debug("Adding filterAttribute: " + filterAttribute
              + " to filterMap with value: " + filterAttValueStr);
          sisFilterMap.put(filterAttribute, filterAttValueStr);
        }
      }

      log.debug("Updated filterMap: " + sisFilterMap);
    }

    // continue
    filterChain.doFilter(req, res);
  }

  public void init(FilterConfig arg0) throws ServletException {
    log.debug("View Filter initialising....");
  }
}

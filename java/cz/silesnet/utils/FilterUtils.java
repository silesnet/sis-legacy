package cz.silesnet.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to help manage filterMap in bound to session.
 *
 * @author Richard Sikora
 */
public class FilterUtils {

    protected static final Log log = LogFactory.getLog(FilterUtils.class);

    //~ Methods ----------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static String getFilterParameter(HttpServletRequest request,
        String attributeName) {
        // get filterMap from session
        Map<String, String> sisFilterMap = (Map<String, String>) request.getSession()
                                                                            .getAttribute("sisFilterMap");
        Assert.notNull(sisFilterMap, "sisFilterMap not present in session!");

        // get filter attribute value from session map
        String returnValue = sisFilterMap.get(attributeName);

        // return value or empty string if attribute not set
        if (returnValue == null)
            returnValue = "";

        return returnValue;
    }
    
    
    /**
     * Retrieves filter parameters from request sessions
     * sisFilterMap with given prefix.
     * 
     * @param request
     * @param filterPrefix
     * @return Map of filter parameters from sessions sisFilterMap starting with prefix.
     * 			Resulting param names are trimed by prefix.
     */
    @SuppressWarnings("unchecked")
	public static Map<String, String> getFilterMap(HttpServletRequest request, String filterPrefix) {
		Map<String, String> resultMap = new HashMap<String, String>();
        // get filterMap from session
        Map<String, String> sisFilterMap = (Map<String, String>) request.getSession().getAttribute("sisFilterMap");
        // adjust prefix if needed
        if (filterPrefix == null)
        	filterPrefix="";
        // iterate sisFilterMap extracting requested filer params into resulting map
        for(String paramName: sisFilterMap.keySet()) {
        	if ("".equals(filterPrefix) || paramName.startsWith(filterPrefix)) {
        		// found requested filter param
        		resultMap.put(paramName.substring(filterPrefix.length()), sisFilterMap.get(paramName));
        	}
        }
		return resultMap;
    }
    
    
    /** @deprecated
     *  WORKS only on GET params!
     *  On Tomcat without <Connector URIEncoding="UTF-8"....> set in server.xml
     */
    public static Map<String, String> getParamMap(HttpServletRequest request, String paramPrefix) {
		Map<String, String> paramMap = new HashMap<String, String>();
		
		String query = request.getQueryString();
		log.debug("Query: " + query);
		// leave if no query present
		if (query == null)
			return paramMap;
		// remove fragment from query if needed
		int fragmentPos = query.lastIndexOf("#");
		if (fragmentPos > -1)
			query = query.substring(0, fragmentPos);
		// tokenize query to param (name=value) chunks
		StringTokenizer params = new StringTokenizer(query, "&");
		// update prefix if needed
		if (paramPrefix == null)
			paramPrefix = "";
		// iterate throught params and extract those with matching prefix
		while (params.hasMoreTokens()) {
			String param = params.nextToken();
			if ("".equals(paramPrefix) || param.startsWith(paramPrefix)) {
				// found requested param
				// split on "="
				String[] p = StringUtils.split(param, "=");
				if (p != null) {
					// name=value pair p[0] name, p[1] value
					// unprefix name first
					String unprefixed = p[0].substring(paramPrefix.length());
					// insert param into result map
					try {
						paramMap.put(URLDecoder.decode(unprefixed, "UTF-8"),URLDecoder.decode(p[1], "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// on errors skip silently
					}
				} else {
					// only name found
					// insert param name into result map
					try {
						paramMap.put(URLDecoder.decode(param.substring(paramPrefix.length()), "UTF-8"), null);
					} catch (UnsupportedEncodingException e) {
						// on errors skip silently
					}
				}
			}
		}
		return paramMap;
	}

}
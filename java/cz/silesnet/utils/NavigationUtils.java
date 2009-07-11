package cz.silesnet.utils;

import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;

/**
 * Utility class to help managing navigation.
 * 
 * @author Richard Sikora
 */
public class NavigationUtils {

	// ~ Static fields/initializers
	// ---------------------------------------------

	protected static final Log log = LogFactory.getLog(NavigationUtils.class);

	// ~ Methods
	// ----------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static String getReturnUrl(HttpServletRequest request,
			String fallbackUrl) {
		StringBuffer returnUrl = new StringBuffer(request.getContextPath());
		// try to get from session _navUrlStack first
		Stack<String> urlStack = (Stack<String>) request.getSession()
				.getAttribute("_navUrlStack");
		if ((urlStack != null) && (!urlStack.empty()))
			returnUrl.append(urlStack.pop());
		else {
			if (fallbackUrl != null)
				returnUrl.append(fallbackUrl);
			else
				return null;
		}
		return returnUrl.toString();
	}

	public static String getReturnUrl(HttpServletRequest request) {
		return getReturnUrl(request, null);
	}

	public static String removeSystemParameters(String query) {
		// remove all parameters from URI query part that starts with _
		if (query == null)
			return null;

		log.debug("Removing system parameters from query: " + query);

		// get framgment from query
		String fragmentString = null;
		int fragmentPosition = query.lastIndexOf('#');

		if (fragmentPosition > -1) {
			fragmentString = query.substring(fragmentPosition);
			query = query.substring(0, fragmentPosition);
		}

		StringTokenizer paramsTokenizer = new StringTokenizer(query, "&");

		StringBuffer cleanQuery = new StringBuffer();

		while (paramsTokenizer.hasMoreTokens()) {
			String paramToken = paramsTokenizer.nextToken();

			if (paramToken.startsWith("_"))
				// system parameter found drop it
				log.debug("Dropping parameter: " + paramToken);
			else
				// normal parameter append it
				cleanQuery.append(paramToken + "&");
		}

		// remove last '&' from query if needed it must be there
		// if buffer is not empty koz we only append to buffer whit '&'
		if (cleanQuery.length() > 0)
			cleanQuery.deleteCharAt(cleanQuery.length() - 1);

		// append fragment if present
		if (fragmentString != null)
			cleanQuery.append(fragmentString);

		log.debug("Returning: " + cleanQuery);

		return cleanQuery.toString();
	}

	public static boolean isTablePagination(HttpServletRequest request) {
		Boolean forceRefresh = (Boolean) request.getSession().getAttribute(
				"forceRefreshList");
		log.debug("Force refresh list: " + forceRefresh);
		if (forceRefresh != null && forceRefresh) {
			request.getSession().setAttribute("forceRefreshList", false);
			return false;
		}
		else
			return WebUtils.getParametersStartingWith(request, "d-").size() == 0 ? false
					: true;
	}

	public static void setForceListRefresh(HttpServletRequest request,
			boolean value) {
		request.getSession().setAttribute("forceRefreshList", value);
	}

	@SuppressWarnings("unchecked")
	public static void clearNavigationStack(HttpServletRequest request) {
		// try to get navigation stack from session
		Stack<String> urlStack = (Stack<String>) request.getSession()
				.getAttribute("_navUrlStack");
		if (urlStack != null)
			urlStack.clear();
	}
}
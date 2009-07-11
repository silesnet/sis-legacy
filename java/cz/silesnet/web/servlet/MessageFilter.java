package cz.silesnet.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Http request filert for handling messages communication between pages.
 * 
 * @author Richard Sikora
 * 
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
package cz.silesnet.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HttpServletRequest wrapper mainly used for URL rewriting.
 * 
 * @author Richard Sikora
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {

	protected final Log log = LogFactory.getLog(getClass());

	public HttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public StringBuffer getRequestURL() {
		// log.debug("URL: " + url);
		// return new StringBuffer(url);
		return super.getRequestURL();
	}

	public String getRequestURI() {
		// log.debug("URL: " + url);
		// return url;
		return super.getRequestURI();
	}
}

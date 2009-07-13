package cz.silesnet.web.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * HttpServletResponse wrapper mailny for URL rewriting.
 * 
 * @author Richard Sikora
 */
public class HttpResponseWrapper extends HttpServletResponseWrapper {

	public HttpResponseWrapper(HttpServletResponse response) {
		super(response);
	}

}

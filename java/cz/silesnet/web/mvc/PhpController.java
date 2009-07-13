package cz.silesnet.web.mvc;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Delegates requests to another web server. Mainly used for php rendered pages.
 * 
 * @author Richard Sikora
 * 
 */
public class PhpController extends AbstractController {

	protected final Log log = LogFactory.getLog(getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("PhpController");
		// URL url = new URL("http://www.silesnet.cz" + request.getRequestURI()
		// + "?" + request.getQueryString());
		StringBuffer delegateUrl = new StringBuffer();
		log.debug(request.getRequestURI());
		log.debug(request.getRequestURL());
		log.debug(request.getServletPath());
		delegateUrl.append("http://www.silesnet.cz").append(
				request.getServletPath());
		if (request.getQueryString() != null)
			delegateUrl.append("?" + request.getQueryString());
		log.debug(delegateUrl);
		URL url = new URL(delegateUrl.toString());
		InputStream is = url.openStream();
		log.debug("Input stream opened");
		for (int data = is.read(); data != -1; data = is.read())
			response.getOutputStream().write((byte) data);
		return null;
	}

}

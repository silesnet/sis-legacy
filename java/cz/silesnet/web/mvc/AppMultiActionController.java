package cz.silesnet.web.mvc;

import cz.silesnet.service.HistoryManager;

import cz.silesnet.utils.SecurityUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Application for various system actions
 * 
 * @author Richard Sikora
 */
public class AppMultiActionController extends MultiActionController {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	private HistoryManager hmgr;

	// ~ Methods
	// ----------------------------------------------------------------

	// injected by Spring
	public void setHistoryManager(HistoryManager historyManager) {
		hmgr = historyManager;
	}

	public ModelAndView redirectDiar(HttpServletRequest request,
			HttpServletResponse response) {
		String destURL = "/php/diar/index.php?" + request.getQueryString();
		log.debug("Redirecting to Diar: " + destURL);

		return new ModelAndView(new RedirectView(destURL, true));
	}

	public ModelAndView redirectMantis(HttpServletRequest request,
			HttpServletResponse response) {
		log
				.debug("Redirecting to mantis.silesnet.cz and trying to auto log in.");

		RedirectView destView = new RedirectView(
				"http://mantis.silesnet.cz/login.php", true);
		Properties props = new Properties();

		// get username
		String userName = SecurityUtils.getUser().getLoginName();

		log.debug("Retrieved username: " + userName);
		props.put("username", userName);
		props.put("password", "sis.sis.sis");
		destView.setAttributes(props);

		return new ModelAndView(destView);
	}

	public ModelAndView viewLastLogin(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		log.debug("View last login list.");
		model.put("historyRecord", hmgr.getLoginHistory());

		return new ModelAndView("app/lastLogin", model);
	}
}
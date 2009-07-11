package cz.silesnet.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility convenience class for getting i18n messages
 * 
 * @author Richar Sikora
 */
public class MessagesUtils implements ApplicationContextAware {

	// ~ Static fields/initializers
	// ---------------------------------------------

	protected static final Log log = LogFactory.getLog(DiffUtils.class);

	private static ApplicationContext ac;

	// ~ Methods
	// ----------------------------------------------------------------

	public static void setCodedSuccessMessage(HttpServletRequest request,
			String key, Object[] args) {
		setPlainResultMessage(request, "successMsg", getMessage(key, args,
				request.getLocale()));
	}

	public static void setCodedSuccessMessage(HttpServletRequest request,
			String key, Object arg) {
		setCodedSuccessMessage(request, key, new Object[] { arg });
	}

	public static void setCodedSuccessMessage(HttpServletRequest request,
			String key) {
		setPlainResultMessage(request, "successMsg", getMessage(key, request
				.getLocale()));
	}

	public static void setCodedFailureMessage(HttpServletRequest request,
			String key, Object[] args) {
		setPlainResultMessage(request, "failureMsg", getMessage(key, args,
				request.getLocale()));
	}

	public static void setCodedFailureMessage(HttpServletRequest request,
			String key, Object arg) {
		setCodedFailureMessage(request, key, new Object[] { arg });
	}

	public static void setCodedFailureMessage(HttpServletRequest request,
			String key) {
		setPlainResultMessage(request, "failureMsg", getMessage(key, request
				.getLocale()));
	}

	public static void setPlainResultMessage(HttpServletRequest request,
			String msgName, String message) {
		// FIXME can not be hardcoded use globals
		request.getSession().setAttribute(msgName, message);
	}

	public static String getMessage(String key, Object[] args, Locale locale) {
		return ac.getMessage(key, args, key, locale);
	}

	public static String getMessage(String key, Object arg, Locale locale) {
		return ac.getMessage(key, new Object[] { arg }, key, locale);
	}

	public static String getMessage(String key, Locale locale) {
		return getMessage(key, null, locale);
	}

	public static String getMessage(String key) {
		return getMessage(key, null, null);
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		log.debug("Setting application context....");
		ac = applicationContext;
	}
}
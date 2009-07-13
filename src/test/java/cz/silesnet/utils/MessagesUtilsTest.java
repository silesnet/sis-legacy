package cz.silesnet.utils;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.silesnet.model.enums.Country;

import java.util.Locale;

/**
 * @author Vlastn�k
 * 
 */
public class MessagesUtilsTest extends TestCase {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	protected ApplicationContext ctx = null;

	// ~ Methods
	// ----------------------------------------------------------------

	public void testMessages() {
		log.debug(MessagesUtils.getMessage(
				"listWireless.label.filter.anyDomain", Locale.ENGLISH));
		log.debug(MessagesUtils.getMessage(Country.CZ.getName(), null));
		log.debug(MessagesUtils.getMessage(Country.CZ.getName()));
		log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
				"cs")));
		log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
				"en")));
		log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
				"it")));

	}

	protected void setUp() {
		String[] paths = { "/WEB-INF/applicationContext-hibernate.xml",
				"/WEB-INF/applicationContext.xml" };
		ctx = new ClassPathXmlApplicationContext(paths);
	}
}
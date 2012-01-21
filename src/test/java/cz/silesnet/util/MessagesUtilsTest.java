package cz.silesnet.util;

import cz.silesnet.model.enums.Country;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Locale;

/**
 * @author Vlastn�k
 */
@Test(groups = "integration")
public class MessagesUtilsTest {

  protected final Log log = LogFactory.getLog(getClass());

  protected ApplicationContext ctx = null;

  @Test
  public void testMessages() {
    log.debug(MessagesUtils.getMessage(Country.CZ.getName(), null));
    log.debug(MessagesUtils.getMessage(Country.CZ.getName()));
    log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
        "cs")));
    log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
        "en")));
    log.debug(MessagesUtils.getMessage(Country.CZ.getName(), new Locale(
        "it")));

  }

  @BeforeTest
  protected void setUp() {
    String[] paths = {"context/sis-messages.xml"};
    ctx = new ClassPathXmlApplicationContext(paths);
  }
}
package cz.silesnet.web;

import cz.silesnet.service.UserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

/**
 * Used to keep record of succesfull logins in application history.
 *
 * @author Richard Sikora
 */
public class LoginListener implements ApplicationListener {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  private UserManager umgr;

  // ~ Methods
  // ----------------------------------------------------------------

  // wired by Spring

  public void setUserManager(UserManager userManager) {
    umgr = userManager;
  }

  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof InteractiveAuthenticationSuccessEvent) {
      log.info("Interactive authentication success.");
      umgr
          .dispatchSuccessfulLoginEvent((InteractiveAuthenticationSuccessEvent) event);
    }

    if (event instanceof HttpSessionCreatedEvent)
      log.info("Http session created.");

    if (event instanceof HttpSessionDestroyedEvent) {
      log.info("Http session destroyed.");
      umgr
          .dispatchSessionDestroyedEvent((HttpSessionDestroyedEvent) event);
    }
  }
}
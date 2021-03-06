package cz.silesnet.service;

import cz.silesnet.model.User;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import java.util.List;

/**
 * User manager interface mainly for logging in users.
 *
 * @author Richard Sikora
 */
public interface UserManager {

  // ~ Methods
  // ----------------------------------------------------------------

  public List<User> getUsers();

  public void dispatchSessionDestroyedEvent(
      HttpSessionDestroyedEvent sessionEvent);

  public void dispatchSuccessfulLoginEvent(
      InteractiveAuthenticationSuccessEvent authEvent);
}
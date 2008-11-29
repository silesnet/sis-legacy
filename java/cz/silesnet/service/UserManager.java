package cz.silesnet.service;

import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.ui.session.HttpSessionDestroyedEvent;

import java.util.List;

/**
 * User manager interface mainly for logging in users.
 *
 * @author Richard Sikora
 */
public interface UserManager {

    //~ Methods ----------------------------------------------------------------

    public List getUsers();

    public void dispatchSessionDestroyedEvent(
        HttpSessionDestroyedEvent sessionEvent);

    public void dispatchSuccessfulLoginEvent(
        InteractiveAuthenticationSuccessEvent authEvent);
}
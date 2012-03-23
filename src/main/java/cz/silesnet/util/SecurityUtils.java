package cz.silesnet.util;

import cz.silesnet.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class using mainly Acegi Security for Spring.
 *
 * @author Richard Sikora
 */
public class SecurityUtils {

    // ~ Methods
    // ----------------------------------------------------------------

    public static User getUser() {
        Object principal = null;

        try {
            principal = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
        } catch (IllegalStateException e) {
        } catch (NullPointerException e) {
        }

        User user = null;

        if (principal != null) {
            if (principal instanceof User)
                user = (User) principal;
            else {
                user = new User();
                user.setId(Long.valueOf(1));
                user.setLoginName(principal.toString());
            }
        } else {
            user = new User();
            user.setId(Long.valueOf(1));
            user.setLoginName("anonymousUser");
        }

        return user;
    }

    public static String currentUser() {
        String user = "anonymous";
        try {
            user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        } catch (Exception e) {
            // ignore
        }
        return user;
    }
}
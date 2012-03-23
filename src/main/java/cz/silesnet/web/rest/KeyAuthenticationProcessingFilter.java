package cz.silesnet.web.rest;

import cz.silesnet.dao.UserDAO;
import cz.silesnet.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * User: der3k
 * Date: 11.3.12
 * Time: 19:46
 */
public class KeyAuthenticationProcessingFilter extends GenericFilterBean {

    private final static GrantedAuthority ROLE_ANONYMOUS = new GrantedAuthorityImpl("ROLE_ANONYMOUS");
    private static final String ANONYMOUS_NAME = "anonymous";
    private static final String AUTHENTICATION_KEY = "key";

    private final Log log = LogFactory.getLog(getClass());

    private UserDAO userDao;

    public void setUserDao(final UserDAO userDao) {
        this.userDao = userDao;
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
        String name = ANONYMOUS_NAME;
        authorities.add(ROLE_ANONYMOUS);
        final String key = ServletRequestUtils.getStringParameter(servletRequest, AUTHENTICATION_KEY);
        log.debug("authenticating user by key: '" + key + "'");
        if (key != null) {
            try {
                final User user = userDao.getUserByKey(key);
                name = user.getName();
                authorities.addAll(user.getAuthorities());
                log.debug("authenticated user " + user);
            } catch (ObjectRetrievalFailureException e) {
                log.debug("authentication failed, assuming default anonymous authentication");
            }
        }
        final String finalName = name;
        final Authentication authentication = new Authentication() {
            public Collection<GrantedAuthority> getAuthorities() {
                return authorities;
            }

            public Object getCredentials() {
                return null;
            }

            public Object getDetails() {
                return null;
            }

            public Object getPrincipal() {
                return finalName;
            }

            public boolean isAuthenticated() {
                return true;
            }

            public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
                throw new IllegalArgumentException();
            }

            public String getName() {
                return finalName;
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

package cz.silesnet.web.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final static GrantedAuthority ROLE_USER = new GrantedAuthorityImpl("ROLE_USER");

    private final Log log = LogFactory.getLog(getClass());

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
        authorities.add(ROLE_ANONYMOUS);
        final String key = ServletRequestUtils.getStringParameter(servletRequest, "key");
        if (key != null)
            authorities.add(ROLE_USER);

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
                return "anonymous";
            }

            public boolean isAuthenticated() {
                return true;
            }

            public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
                throw new IllegalArgumentException();
            }

            public String getName() {
                return "anonymous";
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

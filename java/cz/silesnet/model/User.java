package cz.silesnet.model;

import cz.silesnet.utils.DiffUtils;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

/**
 * User bean.
 *
 * @author Richard Sikora
 *
 */
public class User
    extends Entity
    implements UserDetails {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Log log              = LogFactory.getLog(DiffUtils.class);
    private static final long  serialVersionUID = 5880017008602237629L;

    //~ Instance fields --------------------------------------------------------

    private String             fLoginName;
    private String             fPassword;
    private String             fName;
    private String             fRoles;
    private GrantedAuthority[] fAuthorities;

    //~ Methods ----------------------------------------------------------------

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public void setAuthorities(GrantedAuthority[] authorities) {
        fAuthorities = authorities;
    }

    public GrantedAuthority[] getAuthorities() {
        // if Authorities not polupated yet do it
        if (fAuthorities == null) {
            log.debug("Initializing authorities...");

            String[] roles = StringUtils.commaDelimitedListToStringArray(getRoles());

            fAuthorities = new GrantedAuthority[roles.length];

            for (int i = 0; i < roles.length; i++)
                fAuthorities[i] = new GrantedAuthorityImpl(StringUtils
                                .trimTrailingWhitespace(
                                    StringUtils.trimLeadingWhitespace(roles[i])));
            }

            return fAuthorities;
        }

        public boolean isCredentialsNonExpired() {
            return true;
        }

        public boolean isEnabled() {
            return true;
        }

        public void setLoginName(String loginName) {
            fLoginName = loginName;
        }

        public String getLoginName() {
            return fLoginName;
        }

        public void setName(String name) {
            fName = name;
        }

        public String getName() {
            return fName;
        }

        public void setPassword(String password) {
            fPassword = password;
        }

        public String getPassword() {
            return fPassword;
        }

        public void setRoles(String roles) {
            fRoles = roles;
        }

        public String getRoles() {
            return fRoles;
        }

        public String getUsername() {
            return getLoginName();
        }
    }

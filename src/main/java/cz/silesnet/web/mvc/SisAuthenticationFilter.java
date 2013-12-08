package cz.silesnet.web.mvc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.net.ssl.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 16.11.13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class SisAuthenticationFilter extends GenericFilterBean {
    private final Log log = LogFactory.getLog(getClass());
    private Client client;
    private ObjectMapper mapper;

    private String userUrl;
    private String loginUrl;
    private String sessionId = "JSESSIONID";

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        Assert.hasLength(userUrl);
        Assert.hasLength(loginUrl);
        Assert.hasLength(sessionId);

        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {

                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {

                }
            }
        };

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException(e);
        } catch (KeyManagementException e) {
            throw new ServletException(e);
        }

        HostnameVerifier allHostsValid = new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        };

        final DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(allHostsValid, sslContext));
        client = Client.create(config);

        mapper = new ObjectMapper();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        final Cookie[] cookies = req.getCookies();
        String userData = null;
        if (cookies != null) {
            for (int i = cookies.length; 0 < i; i--) {
                final Cookie cookie = cookies[i - 1];
                if (sessionId.equals(cookie.getName())) {
                    final String url = userUrl + ";jsessionid=" + cookie.getValue();
                    log.debug(url);
                    final WebResource resource = client.resource(url);
                    userData = resource.get(String.class);
                    log.debug(userData);
                    break;
                }
            }
        }
        if (StringUtils.hasLength(userData)) {
            final JsonNode user = mapper.readTree(userData);
            final String principal = user.get("user").getValueAsText();
            final List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.get("roles").getValueAsText());

            Authentication authentication = new AbstractAuthenticationToken(authorities) {

                public Object getCredentials() {
                    return null;
                }

                public Object getPrincipal() {
                    return principal;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }
            };
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(loginUrl);
        }
    }
}

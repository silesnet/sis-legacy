package cz.silesnet.web.mvc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
import javax.servlet.http.HttpSession;
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
public class SisAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
	private final Log log = LogFactory.getLog(getClass());
	private Client client;
	private ObjectMapper mapper;

	private String userUrl;
	private String loginUrl;
	private String sessionId = "JSESSIONID";

	private ApplicationEventPublisher applicationEventPublisher;

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	protected void setClient(Client client) {
		this.client = client;
	}

	protected void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();

		Assert.hasLength(userUrl);
		Assert.hasLength(loginUrl);
		Assert.hasLength(sessionId);

		TrustManager[] trustAllCerts = new TrustManager[]{
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

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
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
		final AbstractAuthenticationToken authentication = authentication(req);
		if (authentication != null) {
			AbstractAuthenticationToken sessionAuthentication = sessionAuthentication(req);
			if (sessionAuthentication != null) {
				if (authentication.getPrincipal().equals(sessionAuthentication.getPrincipal())) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					dropSessionIfPresent(req);
					res.sendRedirect(loginUrl);
					return;
				}
			} else {
				dropSessionIfPresent(req);
				final HttpSession session = req.getSession(true);
				authentication.setDetails(new WebAuthenticationDetails(req));
				session.setAttribute("authentication", authentication);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, SisAuthenticationFilter.class));
			}
			chain.doFilter(request, response);
		} else {
			dropSessionIfPresent(req);
			res.sendRedirect(loginUrl);
		}
	}

	protected AbstractAuthenticationToken authentication(HttpServletRequest request) {
		final String user = fetchUserJsonStringFromRemoteService(request);
		final AbstractAuthenticationToken authentication = authenticationFromJson(user);
		return authentication;
	}

	protected String fetchUserJsonStringFromRemoteService(HttpServletRequest request) {
		final Cookie[] cookies = request.getCookies();
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
		return userData;
	}

	protected AbstractAuthenticationToken authenticationFromJson(String json) {
		AbstractAuthenticationToken authentication = null;
		if (StringUtils.hasLength(json)) {
			final JsonNode node;
			try {
				node = mapper.readTree(json);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			final JsonNode user = node.get("user");
			final JsonNode roles = node.get("roles");
			if (user != null && roles != null) {
				final String principal = user.getValueAsText();
				final List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles.getValueAsText());
				authentication = new AbstractAuthenticationToken(authorities) {

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
			}
		}
		return authentication;
	}

	private AbstractAuthenticationToken sessionAuthentication(HttpServletRequest request) {
		AbstractAuthenticationToken authentication = null;
		final HttpSession session = request.getSession(false);
		if (session != null) {
			authentication = (AbstractAuthenticationToken) session.getAttribute("authentication");
		}
		return authentication;
	}

	private void dropSessionIfPresent(HttpServletRequest req) {
		final HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

}

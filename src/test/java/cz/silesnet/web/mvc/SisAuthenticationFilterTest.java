package cz.silesnet.web.mvc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import cz.silesnet.util.SecurityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SisAuthenticationFilterTest {

	@Test
	public void sessionCreationWhenLogged() throws Exception {
		SisAuthenticationFilter filter = new SisAuthenticationFilter();
		filter.setLoginUrl("http://localhost:8080/login.jsp");
		filter.setUserUrl("http://localhost:8080/get_user");
		filter.setMapper(new ObjectMapper());
		Client client = mock(Client.class);
		filter.setClient(client);
		WebResource resource = mock(WebResource.class);
		when(resource.get(String.class)).thenReturn("{ \"user\" : \"rsi\", \"roles\": \"ROLE_ANONYMOUS\" }");
		when(client.resource("http://localhost:8080/get_user;jsessionid=12345")).thenReturn(resource);

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setCookies(cookies());
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);
		assertNull(req.getSession(false), "no session before filter call");
		filter.doFilter(req, res, chain);

		assertEquals(SecurityUtils.currentUser(), "rsi", "user has been populated to security context");
		assertNotNull(req.getSession(false), "session has been created");
		String principal = (String) ((AbstractAuthenticationToken) req.getSession(false).getAttribute("authentication")).getPrincipal();
		assertEquals(principal, "rsi");
	}

	private Cookie[] cookies() {
		Cookie[] cookies = new Cookie[1];
		cookies[0] = new Cookie("JSESSIONID", "12345");
		return cookies;
	}

}
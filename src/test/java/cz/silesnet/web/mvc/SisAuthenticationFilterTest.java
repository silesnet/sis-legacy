package cz.silesnet.web.mvc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import cz.silesnet.util.SecurityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SisAuthenticationFilterTest {

	@Test
	public void notAuthenticatedWithNoSession() throws Exception {
		SisAuthenticationFilter filter = filterWithAuthResponse("{}");
		MockHttpServletRequest req = requestWithCookies();
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		assertNull(req.getSession(false), "no session before filter call");
		assertNull(res.getRedirectedUrl(), "no redirect before filter call");

		filter.doFilter(req, res, chain);

		assertNull(req.getSession(false), "no session after filter call");
		assertEquals(res.getRedirectedUrl(), "http://localhost:8080/login.jsp", "redirected to login url");
		verify(chain, never()).doFilter(req, res);
	}

	@Test
	public void notAuthenticatedWithExpiredSession() throws Exception {
		SisAuthenticationFilter filter = filterWithAuthResponse("{}");
		MockHttpServletRequest req = requestWithCookies();
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		assertNotNull(req.getSession(true), "session before filter call");
		assertNull(res.getRedirectedUrl(), "no redirect before filter call");

		filter.doFilter(req, res, chain);

		assertNull(req.getSession(false), "no session after filter call");
		assertEquals(res.getRedirectedUrl(), "http://localhost:8080/login.jsp", "redirected to login url");
		verify(chain, never()).doFilter(req, res);
	}

	@Test
	public void authenticatedWithNoSession() throws Exception {
		SisAuthenticationFilter filter = filterWithAuthResponse("{ \"user\" : \"rsi\", \"roles\": \"ROLE_ANONYMOUS\" }");
		MockHttpServletRequest req = requestWithCookies();
		MockHttpServletResponse res = new MockHttpServletResponse();
		ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
		filter.setApplicationEventPublisher(publisher);
		FilterChain chain = mock(FilterChain.class);

		assertNull(req.getSession(false), "no session before filter call");

		filter.doFilter(req, res, chain);

		final HttpSession session = req.getSession(false);
		assertNotNull(session, "session after filter call");
		final Object attribute = session.getAttribute("authentication");
		assertNotNull(attribute, "authentication in session");
		final AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) attribute;
		assertEquals(authentication.getPrincipal(), "rsi", "principal in authentication");
		assertEquals(authentication.getDetails().getClass(), WebAuthenticationDetails.class, "details in authentication");
		assertEquals(SecurityUtils.currentUser(), "rsi", "security context is populated");
		final ArgumentCaptor<ApplicationEvent> captor = ArgumentCaptor.forClass(ApplicationEvent.class);
		verify(publisher).publishEvent(captor.capture());
		final ApplicationEvent event = captor.getValue();
		assertNotNull(event, "event was published");
		assertEquals(event.getSource(), authentication, "event contains authentication");
		verify(chain).doFilter(req, res);
	}

	@Test
	public void authenticatedWithValidSession() throws Exception {
		SisAuthenticationFilter filter = filterWithAuthResponse("{ \"user\" : \"rsi\", \"roles\": \"ROLE_ANONYMOUS\" }");
		MockHttpServletRequest req = requestWithCookies();
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		final AbstractAuthenticationToken authentication = filter.authentication(req);
		assertNotNull(authentication, "authentication exists");

		final HttpSession session = req.getSession(true);
		assertNotNull(session, "session before filter call");
		session.setAttribute("authentication", authentication);

		filter.doFilter(req, res, chain);

		assertEquals(SecurityUtils.currentUser(), "rsi", "security context is populated");
		verify(chain).doFilter(req, res);
	}

	@Test
	public void authenticatedWithMismatchedSession() throws Exception {
		SisAuthenticationFilter filter = filterWithAuthResponse("{ \"user\" : \"rsi\", \"roles\": \"ROLE_ANONYMOUS\" }");
		MockHttpServletRequest req = requestWithCookies();
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		final AbstractAuthenticationToken sessionAuthentication = new UsernamePasswordAuthenticationToken("a", "b");
		assertNotNull(sessionAuthentication, "authentication exists");

		final HttpSession session = req.getSession(true);
		assertNotNull(session, "session before filter call");
		session.setAttribute("authentication", sessionAuthentication);

		filter.doFilter(req, res, chain);

		assertNull(req.getSession(false), "no session after filter call");
		assertEquals(res.getRedirectedUrl(), "http://localhost:8080/login.jsp", "redirected to login url");
		verify(chain, never()).doFilter(req, res);
	}

	private SisAuthenticationFilter filterWithAuthResponse(String response) {
		SisAuthenticationFilter filter = new SisAuthenticationFilter();
		filter.setLoginUrl("http://localhost:8080/login.jsp");
		filter.setUserUrl("http://localhost:8080/get_user");
		filter.setMapper(new ObjectMapper());
		Client client = mock(Client.class);
		filter.setClient(client);
		WebResource resource = mock(WebResource.class);
		when(resource.get(String.class)).thenReturn(response);
		when(client.resource("http://localhost:8080/get_user;jsessionid=12345")).thenReturn(resource);
		return filter;
	}

	private MockHttpServletRequest requestWithCookies() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setCookies(cookies());
		return req;
	}

	private Cookie[] cookies() {
		Cookie[] cookies = new Cookie[1];
		cookies[0] = new Cookie("JSESSIONID", "12345");
		return cookies;
	}



}
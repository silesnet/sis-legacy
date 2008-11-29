package cz.silesnet.web.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.InsufficientAuthenticationException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.ExceptionTranslationFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.NestedServletException;

public class CustomExceptionTransaltionFilter extends ExceptionTranslationFilter {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("HttpServletRequest required");
		}
		
		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("HttpServletResponse required");
		}
		
		try {
			chain.doFilter(request, response);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Chain processed normally");
			}
		} catch (AuthenticationException authentication) {
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication exception occurred; redirecting to authentication entry point",
						authentication);
			}
			sendStartAuthentication(request, response, chain, authentication);
		} catch (AccessDeniedException accessDenied) {
			if (getAuthenticationTrustResolver().isAnonymous(
					SecurityContextHolder.getContext().getAuthentication())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Access is denied (user is anonymous); redirecting to authentication entry point",
							accessDenied);
				}
				
				sendStartAuthentication(request, response, chain,
						new InsufficientAuthenticationException(
						"Full authentication is required to access this resource"));
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Access is denied (user is not anonymous); sending back forbidden response",
							accessDenied);
				}
				
				sendAccessDeniedError(request, response, chain, accessDenied);
			}
		} catch (NestedServletException nestedException) {
			if(nestedException.getCause() instanceof AccessDeniedException) {
				logger.debug("Access is denied");
				sendAccessDeniedError(request, response, chain, (AccessDeniedException) nestedException.getCause());
			} else {
				throw nestedException;
			}
		} catch (ServletException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Throwable otherException) {
			throw new ServletException(otherException);
		}
	}
}

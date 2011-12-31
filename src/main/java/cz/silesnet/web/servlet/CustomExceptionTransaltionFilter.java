package cz.silesnet.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomExceptionTransaltionFilter extends ExceptionTranslationFilter {

    protected final Log logger = LogFactory.getLog(getClass());

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest)) {
            throw new ServletException("HttpServletRequest required");
        }

        if (!(res instanceof HttpServletResponse)) {
            throw new ServletException("HttpServletResponse required");
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(request, response);

            if (logger.isDebugEnabled()) {
                logger.debug("Chain processed normally");
            }
        } catch (AuthenticationException authentication) {
            if (logger.isDebugEnabled()) {
                logger.debug("Authentication exception occurred; redirecting to authentication entry point", authentication);
            }
            sendStartAuthentication(request, response, chain, authentication);
        } catch (NestedServletException nestedException) {
            throw nestedException;
        } catch (ServletException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Throwable otherException) {
            throw new ServletException(otherException);
        }
    }
}

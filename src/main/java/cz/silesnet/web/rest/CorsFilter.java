package cz.silesnet.web.rest;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10.4.12
 * Time: 0:04
 * To change this template use File | Settings | File Templates.
 */
public class CorsFilter extends GenericFilterBean {
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response =  (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

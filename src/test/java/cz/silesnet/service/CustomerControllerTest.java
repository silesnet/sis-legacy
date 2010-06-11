package cz.silesnet.service;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class CustomerControllerTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void testSecurity() {
        String[] paths = {"context/sis-application.xml"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
        assertNotNull(ctx);

        SecurityContext sc = SecurityContextHolder.getContext();
        assertNotNull(sc);
        assertNull(sc.getAuthentication());

        // set authentication in context
        GrantedAuthority roleTest = new GrantedAuthorityImpl("ROLE_FAKE");
        GrantedAuthority[] authorities = new GrantedAuthority[]{roleTest};
        Authentication user = new AnonymousAuthenticationToken("anonymousKey",
                "anonymousUser", authorities);
        assertTrue(user.isAuthenticated());
        sc.setAuthentication(user);

        // we are prepared for calling secured method on container managed bean
        Object controller = ctx.getBean("customerController");
        assertNotNull(controller);

        // use reflection to execute test method
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(controller
                .getClass());
        assertNotNull(methods);
        log.debug(controller.getClass().getName());
        Method test = null;
        for (Method m : methods) {
            log.debug(m.getName());
            if ("showForm".equals(m.getName()))
                test = m;
        }

        log.debug(test);
        try {
            test.invoke(controller, new Object[]{null, null});
            fail();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof AccessDeniedException)
                log.debug("Caught expected exception: " + e.getCause());
            else {
                log.debug(e.getCause());
                // FIXME implement the test correctly
//				fail();
            }
        }
        catch (Throwable e) {
            log.debug(e);
            fail();
        }
    }

}

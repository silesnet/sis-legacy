package cz.silesnet.utils;

import org.acegisecurity.intercept.AbstractSecurityInterceptor;
import org.acegisecurity.intercept.InterceptorStatusToken;
import org.acegisecurity.intercept.ObjectDefinitionSource;
import org.acegisecurity.intercept.method.MethodDefinitionSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;

/**
 * Custome security interceptor to use with AspectJ pointcuts.
 * 
 * @author Richard Sikora
 */
public class BeforeAspectJSecurityInterceptor extends
		AbstractSecurityInterceptor {
	protected final Log log = LogFactory.getLog(getClass());

	private MethodDefinitionSource objectDefinitionSource;
	
	public Class getSecureObjectClass() {
		return JoinPoint.class;
	}

	public ObjectDefinitionSource obtainObjectDefinitionSource() {
		return this.objectDefinitionSource;
	}

	public MethodDefinitionSource getObjectDefinitionSource() {
		return objectDefinitionSource;
	}

	public void setObjectDefinitionSource(
			MethodDefinitionSource objectDefinitionSource) {
		this.objectDefinitionSource = objectDefinitionSource;
	}
	
	public void before(JoinPoint joinPoint) {
		log.debug("Before security test.");
		InterceptorStatusToken token = super.beforeInvocation(joinPoint);
		// cant use with afterInvocationManager koz it's used as before advice
		// look at AspectJSecurityInterceptor for around advice
		super.afterInvocation(token, joinPoint);
		log.debug("After security test.");
	}
}

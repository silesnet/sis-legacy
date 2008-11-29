package cz.silesnet.utils;

import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;
import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Pointcut for AspectJ method security interceptor.
 * 
 * @author Richard Sikora
 */
public aspect MethodSecurityAspect {
	
	private AspectJSecurityInterceptor securityInterceptor;
	
	public void setSecurityInterceptor(AspectJSecurityInterceptor securityInterceptor) {
		this.securityInterceptor = securityInterceptor;
	}
	
	pointcut methodExecution(): target(MultiActionController) 
	&& execution(public org.springframework.web.servlet.ModelAndView *(..)) && !within(MethodSecurityAspect);
	
	Object around(): methodExecution() {
		if (this.securityInterceptor != null) {
			AspectJCallback callback = new AspectJCallback() {
				public Object proceedWithObject() {
					return proceed();
				}
			};
			return this.securityInterceptor.invoke(thisJoinPoint, callback);
		} else {
			return proceed();
		}
	}
	
}

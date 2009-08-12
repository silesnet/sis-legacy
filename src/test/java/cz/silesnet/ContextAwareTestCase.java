package cz.silesnet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test case that loads given Spring application context.
 * 
 * @author Richard Sikora
 * 
 */
public abstract class ContextAwareTestCase extends TestCase {
	private ApplicationContext ctx;

	public ContextAwareTestCase() {
		super();
		loadApplicationContext();
	}

	public ContextAwareTestCase(String name) {
		super(name);
		loadApplicationContext();
	}

	private void loadApplicationContext() {
		ctx = new ClassPathXmlApplicationContext(contextLocations());
	}

	protected abstract String[] contextLocations();

	public ApplicationContext applicationContext() {
		assertThat(ctx, notNullValue());
		return ctx;
	}

}

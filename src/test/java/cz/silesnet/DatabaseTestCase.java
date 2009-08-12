package cz.silesnet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class DatabaseTestCase extends DataSourceBasedDBTestCase {

	private ApplicationContext ctx;

	public DatabaseTestCase() {
		super();
		loadApplicationContext();
	}

	public DatabaseTestCase(String name) {
		super(name);
		loadApplicationContext();
	}

	private void loadApplicationContext() {
		String[] paths = { "/WEB-INF/applicationContext-hibernate.xml" };
		ctx = new ClassPathXmlApplicationContext(paths);
		initializeAfterContextLoaded(ctx);
	}

	protected ApplicationContext applicationContext() {
		assertThat(ctx, notNullValue());
		return ctx;
	}

	protected SessionFactory sessionFactory() {
		assertThat(ctx, notNullValue());
		return (SessionFactory) ctx.getBean("sessionFactory");
	}

	protected abstract void initializeAfterContextLoaded(ApplicationContext ac);

	@Override
	protected DataSource getDataSource() {
		return (DataSource) ctx.getBean("dataSource");
	}

}

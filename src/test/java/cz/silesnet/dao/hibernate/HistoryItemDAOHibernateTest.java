package cz.silesnet.dao.hibernate;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cz.silesnet.DatabaseTestCase;
import cz.silesnet.dao.HistoryItemDAO;

public class HistoryItemDAOHibernateTest extends DatabaseTestCase {

	private HistoryItemDAO auditDao;

	@Override
	protected void initializeAfterContextLoaded(ApplicationContext ac) {
		auditDao = new HistoryItemDAOHibernate();
		((HibernateDaoSupport) auditDao).setSessionFactory(sessionFactory());
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(
				new ClassPathResource("db/audit-items-01.xml").getFile());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRemoveLoginHistoryOlderThan() throws Exception {
		int removedCount = auditDao.removeLoginHistoryOlderThan(new DateTime(
				"2009-08-13"));
		assertThat(removedCount, is(2)); // see details in audit-items-01.xml
	}
}

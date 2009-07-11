package cz.silesnet.utils;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.dao.hibernate.support.SqlHibernateOrder;

public class SearchUtilsTest extends TestCase {

	protected final Log log = LogFactory.getLog(getClass());

	public void testGetTranslateSql() {
		String tss = SearchUtils.getTranslateColumn("column_name");
		log.debug(tss);
	}

	public void testTranslate() {
		String t = "�esk� T��n.;'\"(�)";
		log.debug(t);
		String ts = SearchUtils.translate(t);
		log.debug(ts);
	}

	public void testSqlHibernateOrder() {
		log.debug(SqlHibernateOrder.asc(SearchUtils.getTranslateOrder("name"))
				.toSqlString(null, null));
		log.debug(SqlHibernateOrder.desc(SearchUtils.getTranslateOrder("name"))
				.toSqlString(null, null));
	}

}
package cz.silesnet.dao.hibernate.support;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

/**
 * Order class that uses tranlated i18n columns.
 * 
 * @author Richard Sikora
 */
public class SqlHibernateOrder extends Order {

	private static final long serialVersionUID = 2204134155156562378L;
	
	public SqlHibernateOrder(String orderStr, boolean ascending) {
		super(orderStr, ascending);
	}

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return super.toString();
	}

	public static SqlHibernateOrder asc(String orderStr) {
		return new SqlHibernateOrder(orderStr, true);
	}

	public static SqlHibernateOrder desc(String orderStr) {
		return new SqlHibernateOrder(orderStr, false);
	}
	
}

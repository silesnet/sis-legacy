package cz.silesnet.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;
import cz.silesnet.utils.SearchUtils;

/**
 * Concrete implementation of BillDAO using Hibernate.
 * 
 * @author Richard Sikora
 */
public class BillDAOHibernate extends HibernateDaoSupport implements BillDAO {

	// ~ Methods
	// ----------------------------------------------------------------

	public Bill get(Long billId) {
		Bill bill = (Bill) getHibernateTemplate().get(Bill.class, billId);
		if (bill == null)
			throw new ObjectRetrievalFailureException(Bill.class, billId);
		return bill;
	}

	public Bill get(String uuid) {
		List<Bill> bills = getHibernateTemplate().find(
				"from Bill b where b.hashCode = ?", uuid);

		if ((bills == null) || (bills.size() == 0))
			throw new ObjectRetrievalFailureException(Bill.class, uuid);
		return bills.get(0);
	}

	public void remove(Bill bill) {
		getHibernateTemplate().delete(bill);
	}

	public void removeAll(List<Bill> bills) {
		getHibernateTemplate().deleteAll(bills);
	}

	public void save(Bill bill) {
		getHibernateTemplate().saveOrUpdate(bill);
	}

	public void saveAll(List<Bill> bills) {
		getHibernateTemplate().saveOrUpdateAll(bills);
	}

	public List<Bill> getByStatus(Invoicing invoicing, Boolean isConfirmed,
			Boolean isSent, Boolean isDelivered, Boolean isArchived) {
		DetachedCriteria crit = DetachedCriteria.forClass(Bill.class);
		if (invoicing != null)
			crit.add(Restrictions.eq("invoicingId", invoicing.getId()));
		if (isConfirmed != null)
			crit.add(Restrictions.eq("isConfirmed", isConfirmed));
		if (isSent != null)
			crit.add(Restrictions.eq("isSent", isSent));
		if (isDelivered != null)
			crit.add(Restrictions.eq("isDelivered", isDelivered));
		if (isArchived != null)
			crit.add(Restrictions.eq("isArchived", isArchived));
		// crit.createCriteria("customer").addOrder(Order.asc("name"));
		crit.addOrder(Order.asc("number"));
		return getHibernateTemplate().findByCriteria(crit);
	}

	public List<Bill> getBySentMail(Invoicing invoicing) {
		DetachedCriteria crit = DetachedCriteria.forClass(Bill.class);
		if (invoicing != null)
			crit.add(Restrictions.eq("invoicingId", invoicing.getId()));
		crit.add(Restrictions.eq("isConfirmed", true));
		// crit.add(Restrictions.eq("isSent", true));
		// crit.add(Restrictions.eq("isDelivered", false));
		// crit.add(Restrictions.eq("isArchived", false));
		crit.add(Restrictions.eq("deliverByMail", true));
		crit.addOrder(Order.asc("number"));
		return getHibernateTemplate().findByCriteria(crit);
		// return (ArrayList<Bill>)
		// getHibernateTemplate().find("from Bill b where b.isConfirmed=TRUE and b.isSent=TRUE and b.isDelivered=FALSE and b.isArchived=FALSE and b.customer.billing.deliverByMail=TRUE");
		// return (ArrayList<Bill>)
		// getHibernateTemplate().find("from Bill b where b.isConfirmed=TRUE and b.isSent=TRUE and b.isDelivered=FALSE and b.isArchived=FALSE and b.deliverByMail=TRUE order by b.number asc");
	}

	public List<Bill> getByCustomer(Customer c) {
		return getHibernateTemplate().find(
				"from Bill b where b.customerId=? order by b.number asc",
				c.getId());
	}

	public List<Bill> getByNumber(String billNumber) {
		DetachedCriteria billCrit = DetachedCriteria.forClass(Bill.class);
		SearchUtils.addIlikeRestrictionI18n(billCrit, "number", billNumber);
		billCrit.addOrder(Order.asc("number"));
		return getHibernateTemplate().findByCriteria(billCrit);
	}

	public List<Bill> getByExample(Bill bill) {
		if (bill != null)
			return getByNumber(bill.getNumber());
		else
			return null;
	}

	public Customer fetchCustomer(Bill bill) {
		Customer customer = (Customer) getHibernateTemplate().get(
				Customer.class, bill.getCustomerId());
		return customer;
	}

	public int getCountByStatus(Invoicing invoicing, Boolean isConfirmed,
			Boolean isSent, Boolean isDelivered, Boolean isArchived,
			Boolean isSnail) {
		DetachedCriteria crit = DetachedCriteria.forClass(Bill.class);
		if (invoicing != null)
			crit.add(Restrictions.eq("invoicingId", invoicing.getId()));
		if (isConfirmed != null)
			crit.add(Restrictions.eq("isConfirmed", isConfirmed));
		if (isSent != null)
			crit.add(Restrictions.eq("isSent", isSent));
		if (isDelivered != null)
			crit.add(Restrictions.eq("isDelivered", isDelivered));
		if (isArchived != null)
			crit.add(Restrictions.eq("isArchived", isArchived));
		if (isSnail != null)
			crit.add(Restrictions.eq("deliverByMail", isSnail));
		crit.setProjection(Projections.rowCount());
		return ((Integer) getHibernateTemplate().findByCriteria(crit).get(0))
				.intValue();
	}

	public Bill getToSend(Country country) {
		DetachedCriteria crit = DetachedCriteria.forClass(Bill.class);
		crit.add(Restrictions.eq("isConfirmed", true));
		crit.add(Restrictions.eq("isSent", false));
		crit.add(Restrictions.eq("isArchived", false));
		crit.addOrder(Order.asc("number"));
		String sqlQuery = "SELECT b.* FROM bills as b INNER JOIN invoicings i ON b.invoicing_id = i.id WHERE b.is_confirmed AND NOT b.is_sent AND NOT b.is_archived AND i.country = "
				+ country.getId() + " ORDER BY number LIMIT 1";
		List<Bill> bills = getSession().createSQLQuery(sqlQuery).addEntity(
				Bill.class).list();
		// List<Bill> bills = (ArrayList<Bill>)
		// getHibernateTemplate().findByCriteria(crit, 0, 1);
		if (bills.size() > 0)
			return bills.get(0);
		else
			return null;
	}

	public List<Invoicing> getInvoicings(Country country) {
		DetachedCriteria crit = DetachedCriteria.forClass(Invoicing.class);
		if (country != null)
			crit.add(Restrictions.eq("country", country));
		crit.addOrder(Order.desc("invoicingDate"));
		crit.addOrder(Order.desc("id"));
		return getHibernateTemplate().findByCriteria(crit);
	}

	public Invoicing getInvoicing(Long id) {
		Invoicing invoicing = (Invoicing) getHibernateTemplate().get(
				Invoicing.class, id);
		if (invoicing == null)
			throw new ObjectRetrievalFailureException(Invoicing.class, id);
		return invoicing;
	}

	public void saveInvoicing(Invoicing invoicing) {
		getHibernateTemplate().saveOrUpdate(invoicing);
	}

	public void removeInvoicing(Invoicing invoicing) {
		getHibernateTemplate().delete(invoicing);
	}

	public int getInvoicingSum(Invoicing invoicing) {
		// when no invoicing specified return zero
		if (invoicing == null)
			return 0;
		// TODO Auto-generated method stub
		return 0;
	}

}
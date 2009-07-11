package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of ServiceDAO using Hibernate.
 * 
 * @author Richard Sikora
 */
public class ServiceDAOHibernate extends HibernateDaoSupport implements
		ServiceDAO {

	// ~ Methods
	// ----------------------------------------------------------------

	public List<Service> getAllOrphans() {
		return (ArrayList<Service>) getHibernateTemplate()
				.find(
						"from cz.silesnet.model.Service as s where s.customerId is null");
	}

	public Service get(Long serviceId) {
		return (Service) getHibernateTemplate().get(Service.class, serviceId);
	}

	public void remove(Service service) {
		getHibernateTemplate().delete(service);
	}

	public void save(Service service) {
		getHibernateTemplate().saveOrUpdate(service);
	}

	public int getTotalPrice(Country c) {
		getHibernateTemplate().enableFilter("activeFilter").setParameter(
				"isActive", true);
		Integer total = c == null ? (Integer) getHibernateTemplate().find(
				"select sum(s.price) from Service as s, Customer c"
						+ " where c.id=s.customerId").get(0)
				: (Integer) getHibernateTemplate()
						.find(
								"select sum(s.price) from Service s, Customer c"
										+ " where (c.id=s.customerId) and (c.contact.address.country=?)",
								c).get(0);
		return total != null ? total : 0;
	}

	public int getTotalDownload(Country c) {
		getHibernateTemplate().enableFilter("activeFilter").setParameter(
				"isActive", true);
		Integer total = c == null ? (Integer) getHibernateTemplate().find(
				"select sum(s.connectivity.download) from Service as s, Customer c"
						+ " where c.id=s.customerId").get(0)
				: (Integer) getHibernateTemplate()
						.find(
								"select sum(s.connectivity.download) from Service s, Customer c"
										+ " where c.id=s.customerId and c.contact.address.country=?",
								c).get(0);
		return total != null ? total : 0;
	}

	public int getTotalUpload(Country c) {
		getHibernateTemplate().enableFilter("activeFilter").setParameter(
				"isActive", true);
		Integer total = c == null ? (Integer) getHibernateTemplate().find(
				"select sum(s.connectivity.upload) from Service as s, Customer c"
						+ " where c.id=s.customerId").get(0)
				: (Integer) getHibernateTemplate()
						.find(
								"select sum(s.connectivity.upload) from Service s, Customer c"
										+ " where c.id=s.customerId and c.contact.address.country=?",
								c).get(0);
		return total != null ? total : 0;
	}

	public void evict(Service service) {
		getHibernateTemplate().evict(service);
	}

	public List<Service> getByExample(Service s) {
		if (s == null)
			return null;
		if (s.getPeriod() != null && s.getPeriod().getTo() != null) {
			return (ArrayList<Service>) getHibernateTemplate().find(
					"from Service as s where s.period.to <= ?",
					s.getPeriod().getTo());
		}
		return (ArrayList<Service>) getHibernateTemplate().find("from Service");
	}

}
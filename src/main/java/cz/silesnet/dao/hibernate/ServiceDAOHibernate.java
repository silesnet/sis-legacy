package cz.silesnet.dao.hibernate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;

/**
 * Concrete implementation of ServiceDAO using Hibernate.
 * 
 * @author Richard Sikora
 */
public class ServiceDAOHibernate extends HibernateDaoSupport implements ServiceDAO {

  // ~ Methods
  // ----------------------------------------------------------------

  public List<Service> getAllOrphans() {
    return getHibernateTemplate().find(
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

  public Map<String, Long> calculateSummaryFor(Country c) {
    Map<String, Long> sum = new LinkedHashMap<String, Long>();
    Session session = getSessionFactory().getCurrentSession();
    // count service related statistics
    SQLQuery query = session.createSQLQuery("select sum(case\n" +
        "  when s.bps = 'M' then s.download * 1000\n" +
        "  when s.bps = 'k' then s.download\n" +
        "  else 0\n" +
        "end) as download,\n" +
        "sum(case\n" +
        "  when s.bps = 'M' then s.upload * 1000\n" +
        "  when s.bps = 'k' then s.upload\n" +
        "  else 0\n" +
        "end) as upload,\n" +
        "sum(s.price) as price\n" +
        "from services as s, customers c\n" +
        "where c.id=s.customer_id\n" +
        "and c.country = " + c.getId() + "\n" +
        "and c.is_active")
        .addScalar("download", Hibernate.LONG)
        .addScalar("upload", Hibernate.LONG)
        .addScalar("price", Hibernate.LONG);
    Object[] result = (Object[]) query.uniqueResult();
    sum.put("overviewCustomers.totalDownload", (Long) result[0] / 1000);
    sum.put("overviewCustomers.totalUpload", (Long) result[1] / 1000);
    sum.put("overviewCustomers.totalPrice.CZK", (Long) result[2]);

    // count customers
    query = session.createSQLQuery("select count(id) as customers_count\n" +
        "from customers\n" +
        "where country = " + c.getId() + "\n" +
        "and is_active")
        .addScalar("customers_count", Hibernate.LONG);
    sum.put("overviewCustomers.totalCustomers", (Long) query.uniqueResult());

    return sum;
  }

  public void evict(Service service) {
    getHibernateTemplate().evict(service);
  }

  public List<Service> getByExample(Service s) {
    if (s == null)
      return null;
    if (s.getPeriod() != null && s.getPeriod().getTo() != null) {
      return getHibernateTemplate().find("from Service as s where s.period.to <= ?",
          s.getPeriod().getTo());
    }
    return getHibernateTemplate().find("from Service");
  }

}
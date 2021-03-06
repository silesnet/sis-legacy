package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

/**
 * Concrete implementation of ServiceDAO using Hibernate.
 *
 * @author Richard Sikora
 */
public class ServiceDAOHibernate extends HibernateDaoSupport implements ServiceDAO {

    // ~ Methods
    // ----------------------------------------------------------------


    @Override
    public String findAddressById(long addressId) {
        final Session session = getSessionFactory().getCurrentSession();
        final SQLQuery query = session.createSQLQuery(
            "SELECT label FROM addresses WHERE address_id=" + addressId
        ).addScalar("label", Hibernate.STRING);
        final Object label = query.uniqueResult();
        return label != null ? label.toString() : "";
    }

    public List<Service> getAllOrphans() {
        return getHibernateTemplate().find(
                "from cz.silesnet.model.Service as s where not exists (from cz.silesnet.model.Customer as c where c.id = s.customerId)");
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

        // count customers
        SQLQuery query = session.createSQLQuery("select count(id) as customers_count\n" +
                "from customers\n" +
                "where country = " + c.getId() + "\n" +
                "and is_active")
                .addScalar("customers_count", Hibernate.LONG);
        sum.put("overviewCustomers.totalCustomers", (Long) query.uniqueResult());

        // count service prices
        query = session.createSQLQuery("select \n" +
                "sum(s.price) as price\n" +
                "from services as s inner join customers c on s.customer_id=c.id\n" +
                "where c.country = " + c.getId() + " and (s.period_from <= current_date and (s.period_to >= current_date or s.period_to is null))\n")
                .addScalar("price", Hibernate.LONG);
        sum.put("overviewCustomers.totalServices", (Long) query.uniqueResult());

        // last billing total
        query = session.createSQLQuery("select sum(i.price) price" +
                        " from invoicings bc" +
                        " inner join bills b on b.invoicing_id = bc.id" +
                        " inner join bill_items i on i.bill_id = b.id" +
                        " where bc.id in (select max(id) from invoicings where country = " + c.getId() + ")")
                .addScalar("price", Hibernate.LONG);
        sum.put("overviewCustomers.totalLastInvoicing", (Long) query.uniqueResult());

        return sum;
    }

    public void evict(Service service) {
        getHibernateTemplate().evict(service);
    }

    public Long findMaxIdInRange(long min, long max) {
        Session session = getSessionFactory().getCurrentSession();
        final SQLQuery query = session.createSQLQuery("select max(id) as max_id" +
                " from services where " + min + " <= id and id <= " + max).addScalar("max_id", Hibernate.LONG);
        return (Long) query.uniqueResult();
    }

    public List<Service> getByExample(Service s) {
        if (s == null)
            return null;
        if (s.getContract() != null) {
            final List<Service> services = getHibernateTemplate().find("from Service as s where s.contract = ?", s.getContract());
            final HashMap<Long, Service> unique = new HashMap<Long, Service>();
            for (Service service : services)
                unique.put(service.getCustomerId(), service);
            final List<Service> result = new ArrayList<Service>();
            result.addAll(unique.values());
            return result;
        }
        if (s.getPeriod() != null && s.getPeriod().getTo() != null) {
            return getHibernateTemplate().find("from Service as s where s.period.to <= ?", s.getPeriod().getTo());
        }
        return new ArrayList<Service>(0);
    }

}

package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.dao.hibernate.support.SqlHibernateOrder;
import cz.silesnet.model.Address;
import cz.silesnet.model.Billing;
import cz.silesnet.model.Customer;
import cz.silesnet.model.enums.Country;
import cz.silesnet.utils.SearchUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * Concrete implementation of CustomerDAO using Hibernate.
 *
 * @author Richard Sikora
 */
public class CustomerDAOHibernate extends HibernateDaoSupport implements CustomerDAO {

    protected final Log log = LogFactory.getLog(getClass());

    // ~ Methods
    // ----------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public List<Customer> getAll() {
        DetachedCriteria crit = DetachedCriteria.forClass(Customer.class);
        crit.addOrder(SqlHibernateOrder.asc(SearchUtils
                .getTranslateOrder("name")));
        // get only active customers
        getHibernateTemplate().enableFilter("activeFilter").setParameter(
                "isActive", true);
        return getHibernateTemplate().findByCriteria(crit);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getByExample(Customer cExample) {
        if (cExample == null)
            return null;
        DetachedCriteria crit = DetachedCriteria.forClass(Customer.class);
        // general restrictions
        SearchUtils.addIlikeRestrictionI18n(crit, "name", cExample.getName());
        // SearchUtils.addIlikeRestriction(crit, "public_id",
        // cExample.getPublicId());
        // first 8 digits (excluding '/') are export public_id, use this export
        // public id instead of raw public id
        SearchUtils.addIlikeRestriction(crit, "substring(replace(public_id, '/', ''), 1, 8)", cExample.getExportPublicId());
        SearchUtils.addIlikeInRestriction(crit, "contract_no", cExample.getContractNo().replaceAll("/", ""));
        SearchUtils.addEqRestriction(crit, "symbol", cExample.getSymbol());
        // contact restrictions
        if (cExample.getContact() != null) {
            SearchUtils.addIlikeRestriction(crit, "email", cExample.getContact().getEmail());
            // address restrictions
            if (cExample.getContact().getAddress() != null) {
                Address address = cExample.getContact().getAddress();
                SearchUtils.addIlikeRestrictionI18n(crit, "street", address.getStreet());
                SearchUtils.addIlikeRestrictionI18n(crit, "city", address.getCity());
                SearchUtils.addEqRestriction(crit, "country", address.getCountry());
            }
        }
        // billig restrictions
        if (cExample.getBilling() != null) {
            Billing billing = cExample.getBilling();
            SearchUtils.addEqRestriction(crit, "frequency", billing.getFrequency());
            SearchUtils.addEqRestriction(crit, "is_billed_after", billing.getIsBilledAfter());
            SearchUtils.addEqRestriction(crit, "is_active", billing.getIsActive());
            SearchUtils.addEqRestriction(crit, "status", billing.getStatus());
        }
        // set custom translate ordering
        crit.addOrder(SqlHibernateOrder.asc(SearchUtils.getTranslateOrder("name")));
        return getHibernateTemplate().findByCriteria(crit);
    }

    public void evict(Customer customer) {
        getHibernateTemplate().evict(customer);
    }

    public Customer get(Long customerId) {
        Customer customer = (Customer) getHibernateTemplate().get(
                Customer.class, customerId);

        if (customer == null)
            throw new ObjectRetrievalFailureException(Customer.class,
                    customerId);

        return customer;
    }

    public Customer load(Long customerId) {
        Customer customer = (Customer) getHibernateTemplate().load(
                Customer.class, customerId);
        if (customer == null)
            throw new ObjectRetrievalFailureException(Customer.class,
                    customerId);
        return customer;
    }

    public void remove(Customer customer) {
        getHibernateTemplate().delete(customer);
    }

    public void save(Customer customer) {
        getHibernateTemplate().saveOrUpdate(customer);
    }

    public void saveAll(List<Customer> customers) {
        getHibernateTemplate().saveOrUpdateAll(customers);
    }

    public int getTotalCustomers(Country c) {
        getHibernateTemplate().enableFilter("activeFilter").setParameter(
                "isActive", true);
        Integer total = c == null ? (Integer) getHibernateTemplate().find(
                "select count(*) from Customer").get(0)
                : (Integer) getHibernateTemplate().find(
                "select count(*) from Customer c"
                        + " where c.contact.address.country=?", c).get(
                0);
        return total != null ? total : 0;
    }

    @SuppressWarnings("unchecked")
    public Iterable<Long> findActiveCustomerIdsByCountry(final Country country) {
        getHibernateTemplate().enableFilter("activeFilter").setParameter("isActive", true);
        List ids = getHibernateTemplate().find(
                "select c.id from Customer c where c.contact.address.country=? order by c.name", country);
        return (Iterable<Long>) ids;
    }

}
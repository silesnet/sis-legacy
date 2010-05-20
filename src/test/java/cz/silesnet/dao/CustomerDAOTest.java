package cz.silesnet.dao;

import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.enums.Frequency;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class CustomerDAOTest extends DaoTestSupport<CustomerDAO> {

    @Test
    public void testCRUD() {

        Customer c = PrepareMixture.getCustomer();
        int servicesSize = c.getServices().size();
        String customerName = c.getName();
        Frequency billingFrequency = c.getBilling().getFrequency();

        dao.save(c);
        assertThat(c.getId(), is(not(nullValue())));

        // get persisted object id and drop it
        Long cId = c.getId();

        // retrieve persisted customer
        c = dao.get(cId);
        assertThat(c.getId(), is(not(nullValue())));
        assertThat(c.getContact(), is(not(nullValue())));
        assertThat(c.getServices().size(), is(servicesSize));
        assertThat(c.getServices().get(0).getName(), is(not(nullValue())));
        assertThat(c.getServices().get(1).getName(), is(not(nullValue())));
        assertThat(c.getBilling(), is(not(nullValue())));
        assertThat(c.getName(), is(customerName));
        assertThat(c.getBilling().getFrequency(), is(billingFrequency));

        // update
        c.setName("Modified Customer Name");
        c.getContact().getAddress().setStreet("Modified Street");
        c.getContact().setEmail("admin@gymopol.cz");
        c.getServices().remove(0);
        c.getServices().get(0).setInfo("Modified Service Info String");
        c.getServices().get(0).setFrequency(Frequency.WEEKLY);
        c.getBilling().setFrequency(Frequency.DAILY);

        // persist
        dao.save(c);

        // reget
        c = dao.get(cId);
        assertThat(c.getId(), is(not(nullValue())));
        assertThat(c.getContact(), is(not(nullValue())));
        assertThat(c.getServices().size(), is(servicesSize - 1));
        assertThat(c.getServices().get(0).getName(), is(not(nullValue())));
        assertThat(c.getBilling(), is(not(nullValue())));
        assertThat("Modified Customer Name".equals(c.getName()), is(true));
        assertThat("Modified Street".equals(c.getContact().getAddress().getStreet()), is(true));
        assertThat("admin@gymopol.cz".equals(c.getContact().getEmail()), is(true));
        assertThat("Modified Service Info String".equals(c.getServices().get(0).getInfo()), is(true));
        assertThat((c.getBilling().getFrequency().equals(Frequency.DAILY)), is(true));

        // does not work with non transactional session, see
        // HibernateTemplate.enableFilter()
        // List<Customer> customers = dao.getAll();
        List<Customer> customers = null;

        // empty filter should return all
        Customer filterCustomer = new Customer();
        // FIXME should work with constructor pre filled enum fields
        filterCustomer.setContact(null);
        filterCustomer.setBilling(null);
        customers = dao.getByExample(filterCustomer);
        assertThat(customers.size() >= 1, is(true));

        // with existing name should return it
        filterCustomer.setName("Modified Customer Name");
        customers = dao.getByExample(filterCustomer);
        assertThat(customers.size() >= 1, is(true));

        // with non existing name should return empty list
        filterCustomer.setName("xxx");
        customers = dao.getByExample(filterCustomer);
        assertThat(customers.size() == 0, is(true));

        // remove
        dao.remove(c);

        // try to reget
        try {
            dao.get(cId);
            throw new Error();
        }
        catch (ObjectRetrievalFailureException e) {
            // expected
        }
    }

}
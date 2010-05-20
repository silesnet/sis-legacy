package cz.silesnet.dao;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class BillDAOTest extends DaoTestSupport<BillDAO> {

    @Test
    public void CRUD() {
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setName("Customer 1");

        Bill b = PrepareMixture.getBillSimple();
        b.setInvoicedCustomer(customer);
        String billHash = b.getHashCode();

        dao.save(b);
        assertThat(b.getId(), is(not(nullValue())));
        Long billId = b.getId();

        // update
        b.getItems().get(0).setText("Modified First Line");
        b.setIsConfirmed(true);
        String secondLine = b.getItems().get(1).getText();

        // persist changes
        dao.save(b);

        // try to retrieve it
        b = dao.get(billId);
        assertThat(b, is(not(nullValue())));
        assertThat(b.getItems().get(0).getText(), is("Modified First Line"));
        assertThat(b.getItems().get(1).getText(), is(secondLine));
        assertThat(b.getInvoicedCustomer(), is(not(nullValue())));

        b = null;
        try {
            b = dao.get("hashxxWRONG");
            throw new Error("Retrieved bill by non existing hashcode");
        }
        catch (ObjectRetrievalFailureException e) {
            // expected
        }
        assertThat(b, is(nullValue()));

        b = dao.get(billHash);
        assertThat(b.getId(), is(billId));

        // tidy up
        dao.remove(b);

        b = null;
        try {
            b = dao.get(billId);
            throw new Error("Retrieved deleted bill!");
        }
        catch (ObjectRetrievalFailureException e) {
            // expected
        }
        assertThat(b, is(nullValue()));
    }

}

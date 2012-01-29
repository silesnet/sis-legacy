package cz.silesnet.model;

import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CustomerTest {

    private static final long SERVICE1_ID_NEW_CONTRACT_101 = 10010101;
    private static final long SERVICE2_ID_NEW_CONTRACT_102 = 10010201;
    private static final long SERVICE3_ID_SAME_CONTRACT_102 = 10010202;
    private static final long SERVICE4_ID_ONETIME_SAME_CONTRACT_101 = 110010101;
    private static final long SERVICE5_ID_ONETIME_NEW_CONTRACT_103 = 110010301;

    @Test
    public void testShouldReturnEmptyContractNoWhenNoOrNullServices() throws Exception {
        final Customer customer = new Customer();
        assertThat(customer.getContractNo(), is(""));
        customer.setServices(null);
        assertThat(customer.getContractNo(), is(""));
    }

    @Test
    public void testShouldReturnContractNumbersFromServices() throws Exception {
        final Service service1 = new Service();
        service1.setId(SERVICE1_ID_NEW_CONTRACT_101);
        final Service service2 = new Service();
        service2.setId(SERVICE2_ID_NEW_CONTRACT_102);
        final Service service3 = new Service();
        service3.setId(SERVICE3_ID_SAME_CONTRACT_102);
        final Service service4 = new Service();
        service4.setId(SERVICE4_ID_ONETIME_SAME_CONTRACT_101);
        final Service service5 = new Service();
        service5.setId(SERVICE5_ID_ONETIME_NEW_CONTRACT_103);
        final Customer customer = new Customer();
        customer.getServices().add(service1);
        customer.getServices().add(service2);
        customer.getServices().add(service3);
        customer.getServices().add(service4);
        customer.getServices().add(service5);
        assertThat(customer.getContractNo(), is("101, 102, 103"));
    }

    @Test
    public void testIsSpsSynchronizedTrue() {
        Customer customer = new Customer();
        long now = System.currentTimeMillis();
        customer.setUpdated(new Date(now));
        customer.setSynchronized(new Date(now + 1));
        assertTrue(customer.isSpsSynchronized());
    }

    @Test
    public void testIsSpsSynchronizedGreater() {
        Customer customer = new Customer();
        long now = System.currentTimeMillis();
        customer.setUpdated(new Date(now));
        customer.setSynchronized(new Date(now - 1));
        assertFalse(customer.isSpsSynchronized());
    }

    @Test
    public void testIsSpsSynchronizedEqual() {
        Customer customer = new Customer();
        Date now = new Date();
        customer.setUpdated(now);
        customer.setSynchronized(now);
        assertFalse(customer.isSpsSynchronized());
    }

    @Test
    public void testIsSpsSynchronizedNull() {
        Customer customer = new Customer();
        assertFalse(customer.isSpsSynchronized());
    }

}

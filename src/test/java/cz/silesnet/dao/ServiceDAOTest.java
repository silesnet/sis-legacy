package cz.silesnet.dao;

import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Frequency;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataSet("/cz/silesnet/dao/ServiceDAOTest.xml")
public abstract class ServiceDAOTest extends DaoTestSupport<ServiceDAO> {
    @Test
    public void testCRUD() {
        Service service = PrepareMixture.getService();
        // persist service
        dao.save(service);
        assertThat(service.getId(), is(notNullValue()));
        // get it
        Service service2 = dao.get(service.getId());
        assertThat(service2, is(notNullValue()));
        assertThat(service2.getFrequency(), is(Frequency.MONTHLY));
        // get all
        List<Service> services = dao.getAllOrphans();
        assertThat(services.size(), is(greaterThanOrEqualTo(1)));
        // remove it
        dao.remove(service);
        // try to reget it after removing
        Service service3 = dao.get(service.getId());
        assertThat(service3, is(nullValue()));
    }

    @Test
    public void testGetAllOrphans() throws Exception {
        List<Service> orphans = dao.getAllOrphans();
        assertThat(orphans.size(), is(1));
        assertThat(orphans.get(0).getName(), is("Service 5"));
    }

    @Test
    public void testCalculateSummaryFor() throws Exception {
        assertThat(true, is(false));
    }

    @Test
    public void testEvict() throws Exception {
        assertThat(true, is(false));
    }

    @Test
    public void testGetByExample() throws Exception {
        assertThat(true, is(false));
    }
}
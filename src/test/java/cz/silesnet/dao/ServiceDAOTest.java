package cz.silesnet.dao;

import cz.silesnet.model.Period;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@DataSet("/cz/silesnet/dao/ServiceDAOTest.xml")
public abstract class ServiceDAOTest extends DaoTestSupport<ServiceDAO> {

  @Test
  public void testGetAllOrphans() throws Exception {
    List<Service> orphans = dao.getAllOrphans();
    assertThat(orphans.size(), is(1));
    assertThat(orphans.get(0).getName(), is("Service 6"));
  }

  @Test
  public void testCalculateSummaryFor() throws Exception {
    Map<String, Long> summary = dao.calculateSummaryFor(Country.CZ);
    assertThat(summary.get("overviewCustomers.totalCustomers"), is(2L));
    assertThat(summary.get("overviewCustomers.totalDownload"), is(4L));
    assertThat(summary.get("overviewCustomers.totalUpload"), is(3L));
    assertThat(summary.get("overviewCustomers.totalPrice.CZK"), is(300L));
  }

  @Test
  public void testEvict() throws Exception {
    // do not know as to test Hibernate evict from cache operation
    // perhaps it should not be in the DAO interface
    // TODO resolve the issue
    assertThat(true, is(true));
  }

  @Test
  public void testGetByExample() throws Exception {
    Service service = new Service();
    Calendar cal = new GregorianCalendar(2011, Calendar.JANUARY, 1);
    service.setPeriod(new Period(null, cal.getTime()));

    List<Service> services = dao.getByExample(service);
    Service service2 = dao.get(12L);
//    log.debug(service2);
    assertThat("error in fixture configuration! (dbunit.org)", service2.getPeriod().getTo(), is(not(nullValue())));
    assertThat(services.size(), is(1));
    assertThat(services.get(0).getName(), is("Service 3"));
  }
}
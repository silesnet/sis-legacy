package cz.silesnet.util;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.Date;

public class HistoricToStringTest {

  protected final Log log = LogFactory.getLog(HistoricToStringTest.class);

  @Test
  public void testToString() {

    Label serviceLabel = new Label();
    serviceLabel.setName("Test service Label");
    log.debug(serviceLabel.getHistoricToString());

    Address address = new Address();
    address.setStreet("Havlickova 13");
    address.setCity("Cesky Tesin");
    address.setPostalCode("737 01");
    address.setCountry(Country.CZ);
    log.debug(address.getHistoricToString());

    Contact contact = new Contact();
    contact.setAddress(address);
    contact.setName("Renata Petrakova");
    contact.setEmail("info@gympol.cz");
    contact.setPhone("558 555 333");
    log.debug(contact.getHistoricToString());

    // prepare service object
    Service service = new Service();
    service.setPeriod(new Period(new Date(), null));
    service.setName("wirelessHOME");
    service.setPrice(Integer.valueOf(641));
    service.setFrequency(Frequency.MONTHLY);
    service.setInfo("Service info");

    log.debug(service.getHistoricToString());
  }

}

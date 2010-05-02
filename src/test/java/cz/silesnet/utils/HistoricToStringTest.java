package cz.silesnet.utils;

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.model.Address;
import cz.silesnet.model.Connectivity;
import cz.silesnet.model.Contact;
import cz.silesnet.model.Label;
import cz.silesnet.model.Period;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;

public class HistoricToStringTest extends TestCase {

  protected final Log log = LogFactory.getLog(getClass());

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
    service.setConnectivity(new Connectivity(512, 256));
    service.setInfo("Service info");

    log.debug(service.getHistoricToString());
  }

}

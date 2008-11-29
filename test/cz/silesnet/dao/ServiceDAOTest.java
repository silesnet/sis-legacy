package cz.silesnet.dao;

import java.util.List;

import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Frequency;

public class ServiceDAOTest
    extends BaseDAOTestCase {

    //~ Methods ----------------------------------------------------------------

    public void testCRUD() {
        // get dao implementation from application context
        ServiceDAO   dao  = (ServiceDAO) ctx.getBean("serviceDAO");

        // prepare service object
        Service service = PrepareMixture.getService();

        // persist service
        dao.save(service);
        assertNotNull(service.getId());

        // get it
        Service service2 = dao.get(service.getId());
        assertNotNull(service2);
        assertTrue(service2.getFrequency().equals(Frequency.MONTHLY));
        log.debug(service2);
        log.debug(service2.getHistoricToString());

        // get all
        List<Service> services = dao.getAllOrphans();
        assertTrue(services.size() >= 1);
        
        // remove it
        dao.remove(service);

        // try to reget it after removing
        Service service3 = dao.get(service.getId());
        assertNull(service3);
    }
    
}
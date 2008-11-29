package cz.silesnet.dao;

import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;

import java.util.List;

/**
 * DAO persistence interface for service entity classes.
 *
 * @author Richard Sikora
 */
public interface ServiceDAO
    extends DAO {

    //~ Methods ----------------------------------------------------------------

    public List<Service> getAllOrphans();

    public List<Service> getByExample(Service service);

    public Service get(Long serviceId);

    public void remove(Service service);

    public void save(Service service);

    public int getTotalPrice(Country c);

    public int getTotalDownload(Country c);

    public int getTotalUpload(Country c);
    
    public void evict(Service service);

}
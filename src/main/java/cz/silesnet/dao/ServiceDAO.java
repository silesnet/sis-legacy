package cz.silesnet.dao;

import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;

import java.util.List;
import java.util.Map;

/**
 * DAO persistence interface for service entity classes.
 *
 * @author Richard Sikora
 */
public interface ServiceDAO extends DAO {

  // ~ Methods
  // ----------------------------------------------------------------

  public List<Service> getAllOrphans();

  public List<Service> getByExample(Service service);

  public Service get(Long serviceId);

  public void remove(Service service);

  public void save(Service service);

  public Map<String, Long> calculateSummaryFor(Country c);

  public void evict(Service service);

}
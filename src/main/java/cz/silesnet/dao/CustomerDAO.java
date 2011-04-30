package cz.silesnet.dao;

import cz.silesnet.model.Customer;
import cz.silesnet.model.enums.Country;

import java.util.Iterator;
import java.util.List;

/**
 * Interface for Customers persistence operations.
 *
 * @author Richard Sikora
 */
public interface CustomerDAO extends DAO {

  // ~ Methods
  // ----------------------------------------------------------------

  public List<Customer> getAll();

  public List<Customer> getByExample(Customer customerExample);

  public void evict(Customer customer);

  public Customer get(Long customerId);

  public Customer load(Long customerId);

  public void remove(Customer customer);

  public void save(Customer customer);

  public void saveAll(List<Customer> customers);

  public int getTotalCustomers(Country c);

  public Iterable<Long> findActiveCustomerIdsByCountry(Country country);

}
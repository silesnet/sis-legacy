package cz.silesnet.service;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Service;
import cz.silesnet.model.ServiceBlueprint;
import cz.silesnet.model.enums.Country;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Customer manager interface declaring service methods on customers.
 *
 * @author Richard Sikora
 */
public interface CustomerManager {

    // ~ Methods
    // ----------------------------------------------------------------

    public List<Customer> getAll();

    public List<Customer> getByExample(Customer customer);

    public List<Customer> getByExample(Customer customer, Service service);

    public void delete(Customer customer);

    public Customer get(Long customerId);

    public void insert(Customer customer);

    public void update(Customer customer);

    public Map<String, Long> getSummaryFor(Country c);

    public Service getService(Long serviceId);

    public void insertService(Service service);

    public void addService(ServiceBlueprint blueprint, int price);

    public void updateService(Service service);

    public void deleteService(Service service);

    public void deactivateCandidates();

    public List<Bill> fetchBills(Customer customer);

    public void updateSymbol(Customer customer);

    public void exportCustomersToInsert(List<Customer> customers, PrintWriter writer);
}
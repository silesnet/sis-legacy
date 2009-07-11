package cz.silesnet.service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;

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

	public void updateAll(List<Customer> customers);

	public Map<String, String> getOverview(Country c);

	public void exportCusotmersToWinDuo(List<Customer> customers,
			PrintWriter writer);

	public Service getService(Long serviceId);

	public void insertService(Service service);

	public void updateService(Service service);

	public void deleteService(Service service);

	public void deactivateCandidates();

	public List<Bill> fetchBills(Customer customer);

	public void updateSymbol(Customer customer);

	public void exportCusotmersToInsert(List<Customer> customers,
			PrintWriter writer);
}
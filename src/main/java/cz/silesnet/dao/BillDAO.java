package cz.silesnet.dao;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;

import java.util.List;

/**
 * Interface for Bill persistence manipulation.
 *
 * @author Richard Sikora
 */
public interface BillDAO extends DAO {

  // ~ Methods
  // ----------------------------------------------------------------

  public List<Bill> getByStatus(Invoicing invoicing, Boolean isConfirmed,
                                Boolean isSent, Boolean isDelivered, Boolean isArchived);

  public List<Bill> getBySentMail(Invoicing invoicing);

  public List<Bill> getByCustomer(Customer c);

  public List<Bill> getByNumber(String billNumber);

  public List<Bill> getByExample(Bill bill);

  public Bill get(Long billId);

  public Bill get(String uuid);

  public void remove(Bill bill);

  public void removeAll(List<Bill> bills);

  public void save(Bill bill);

  public void saveAll(List<Bill> bills);

  public Customer fetchCustomer(Bill bill);

  public int getCountByStatus(Invoicing invoicing, Boolean isConfirmed,
                              Boolean isSent, Boolean isDelivered, Boolean isArchived,
                              Boolean isSnail);

  public Bill getToSend(Country country);

  public List<Invoicing> getInvoicings(Country country);

  public Invoicing getInvoicing(Long id);

  public void saveInvoicing(Invoicing invoicing);

  public void removeInvoicing(Invoicing invoicing);

  public int getInvoicingSum(Invoicing invoicing);

}
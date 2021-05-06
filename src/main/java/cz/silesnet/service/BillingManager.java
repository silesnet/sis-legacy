package cz.silesnet.service;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;
import org.apache.commons.lang.mutable.MutableInt;
import org.springframework.mail.MailException;

import java.io.PrintWriter;
import java.util.List;

public interface BillingManager {
  public void setReminderSenderFlag(boolean flag);

 public boolean getReminderSenderFlag();

  public void billCustomersIn(Invoicing invoicing);

  public void sendNextInvoice();

  public void email(Bill bill, String recipient) throws MailException;

  public void emailAll(List<Bill> bills);

  public Bill send(Bill bill, MutableInt emailedCounter);

  public void reSendAll(Invoicing invoicing, List<Bill> bills);

  public Bill confirmDelivery(String uuid);

  public boolean getSendingEnabled(Country country);

  public void setSendingEnabled(boolean status, Country country);

  public void exportAllToInsert(Invoicing invoicing, List<Bill> bills, PrintWriter writer);


  public Invoicing getInvoicing(Long id);

  public void insertInvoicing(Invoicing invoicing);

  public List<Invoicing> getInvoicings(Country country);

  public List<Bill> getInvoices(Invoicing invoicing);

  public int getInvoicingSum(Invoicing invoicing);


  public Bill get(Long billId);

  public void insert(Bill bill);

  public void update(Bill bill);

  public Customer fetchCustomer(Bill bill);

  public List<Bill> getByCustomer(Customer c);

  public List<Bill> getBySentMail(Invoicing invoicing);

  public List<Bill> getByExample(Bill bill);

  public List<Bill> getByStatus(Invoicing invoicing, Boolean isConfirmed, Boolean isSent,
                                Boolean isDelivered, Boolean isArchived);

  public int getCountByStatus(Invoicing invoicing, Boolean isConfirmed, Boolean isSent,
                              Boolean isDelivered, Boolean isArchived, Boolean isSnail);

 String getServiceAddressLabel(Long serviceId);
}

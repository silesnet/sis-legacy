package cz.silesnet.service.impl;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.model.invoice.Accountant;
import cz.silesnet.model.invoice.BillingContext;
import cz.silesnet.model.invoice.BillingContextFactory;
import cz.silesnet.model.invoice.BillingResult;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.SettingManager;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.mail.MimeMessagePreparatorFactory;
import cz.silesnet.utils.MessagesUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.orm.ObjectRetrievalFailureException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Concrete implementation of Billing manager using mainly BillDAO.
 *
 * @author Richard Sikora
 */
public class BillingManagerImpl implements BillingManager {
  private static final int VAT_PL = 23;
  private static final Map<String, String> MESSAGE_KEYS_MAPPING = errorsAndWarningsKeysMap();
  private static final SimpleDateFormat AUDIT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

  protected final Log log = LogFactory.getLog(getClass());

  private BillDAO dao;

  private CustomerManager cMgr;

  private CustomerDAO customerDao;

  private HistoryManager hmgr;

  private SettingManager setMgr;

  private MimeMessagePreparatorFactory messagePreparatorFactory;

  private JavaMailSender mailSender;

  private String emailFromAddressCs;

  private int emailSendingDelay = 5;

  public static final int sPrecison = 10000;

  private static int sVat = 19;

  private static int sPurgeDateDays = 14;

  private BillingContextFactory billingContextFactory;

  public void setBillingContextFactory(final BillingContextFactory billingContextFactory) {
    this.billingContextFactory = billingContextFactory;
  }

  public void setCustomerManager(CustomerManager mgr) {
    cMgr = mgr;
  }

  public void setEmailSendingDelay(int emailSendingDelay) {
    this.emailSendingDelay = emailSendingDelay;
  }

  public void setBillDAO(BillDAO billDAO) {
    dao = billDAO;
  }

  public void setHistoryManager(HistoryManager historyManager) {
    hmgr = historyManager;
  }

  public void setCustomerDao(final CustomerDAO customerDao) {
    this.customerDao = customerDao;
  }

  public void setSettingManager(SettingManager settingManager) {
    setMgr = settingManager;
    sVat = setMgr.getInteger("billing.vat.cz", 19);
    log.debug("VAT set to: " + sVat);
    sPurgeDateDays = setMgr.getInteger("billing.purgeDateDays.cz", 14);
    log.debug("PurgeDateDays set to: " + sPurgeDateDays);
  }

  public void setMessagePreparatorFactory(final MimeMessagePreparatorFactory messagePreparatorFactory) {
    this.messagePreparatorFactory = messagePreparatorFactory;
  }

  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void setEmailFromAddressCs(String email) {
    this.emailFromAddressCs = email;
  }

  public Bill get(Long billId) {
    return dao.get(billId);
  }

  public List<Bill> getByStatus(Invoicing invoicing, Boolean isConfirmed,
                                Boolean isSent, Boolean isDelivered, Boolean isArchived) {
    return dao.getByStatus(invoicing, isConfirmed, isSent, isDelivered,
        isArchived);
  }

  public List<Bill> getByCustomer(Customer c) {
    return dao.getByCustomer(c);
  }

  public List<Bill> getBySentMail(Invoicing invoicing) {
    return dao.getBySentMail(invoicing);
  }

  public List<Bill> getByNumber(String billNumber) {
    return dao.getByNumber(billNumber);
  }

  public List<Bill> getByExample(Bill bill) {
    return dao.getByExample(bill);
  }

  public void insert(Bill bill) {
    dao.save(bill);
  }

  public void insertAll(List<Bill> bills) {
    dao.saveAll(bills);
  }

  public void delete(Bill bill) {
    dao.remove(bill);
  }

  public void update(Bill bill) {
    dao.save(bill);
  }

  public List<Bill> generateAll(Invoicing invoicing, List<Customer> customers, Date due, String lastInvoiceNo) {
    // fail fast
    if (due == null || lastInvoiceNo == null)
      throw new NullPointerException(
          "Invoicing date or last invoice number not set.");
    // set bills numbering base
    Long nextInvoiceNo;
    // can throw bad formatting runtime exception
    nextInvoiceNo = Long.valueOf(lastInvoiceNo) + 1;
    // so let's start here by initializing some vars
    int skipped = 0;
    int errors = 0;
    int all = customers.size();
    Iterator<Service> sIter = null;
    Bill newInvoice = null;
    List<Bill> invoices = new ArrayList<Bill>();
    Iterator<Customer> cIter = customers.iterator();
    // log this big event
    String invoicingLog = all + ", "
        + AUDIT_DATE_FORMAT.format(due) + ", "
        + lastInvoiceNo;
    log.info("Invoicing customers (" + invoicingLog + ") STARTED...");
    hmgr.insertSystemBillingAudit(invoicing, null,
        "mainBilling.msg.billingStarted", invoicingLog + " ***");
    // iterate over customers and try to invoice them
    while (cIter.hasNext()) {
      Customer customer = cIter.next();
      if (!customer.getBilling().getIsActive()) {
        // silently skip inactive customers
        log.info("Skipping customer (INACTIVE): " + customer.getName());
        cIter.remove();
        // decrement all considered customers
        all--;
        continue;
      }
      if (!BillingStatus.INVOICE
          .equals(customer.getBilling().getStatus())) {
        // skip no billFor cusomers
        log.info("Skipping customer (BILLING DISABLED): "
            + customer.getName());
        hmgr.insertSystemBillingAudit(invoicing, customer,
            "mainBilling.msg.billinDisabled",
            "mainBilling.status.skipped");
        cIter.remove();
        skipped++;
        continue;
      }
      try {
        // generate invoice for the customer, can be null
        newInvoice = generate(customer, due, nextInvoiceNo.toString());
      } catch (RuntimeException e) {
        // some periods are invalid
        log.info("Skipping customer (INVOICING ERROR): "
            + customer.getName());
        hmgr.insertSystemBillingAudit(invoicing, customer,
            "mainBilling.msg.illegalArgument",
            "mainBilling.status.error");
        cIter.remove();
        errors++;
        continue;
      }
      if (newInvoice == null) {
        // skip customers with no invoice for this due date
        log.info("Skipping customer (NO INVOICE): "
            + customer.getName());
        hmgr.insertSystemBillingAudit(invoicing, customer,
            "mainBilling.msg.noInvoice",
            "mainBilling.status.skipped");
        cIter.remove();
        skipped++;
        continue;
      }
      if (newInvoice.getTotalPrice() == 0) {
        // skip customers with zero invoice amount unless there is
        // ONE_TIME service
        boolean oneTimePresent = false;
        for (Service s : customer.getServices()) {
          if (Frequency.ONE_TIME.equals(s.getFrequency()))
            oneTimePresent = true;
        }
        if (!oneTimePresent) {
          log.info("Skipping customer (ZERO INVOICE, NO ONE_TIME): "
              + customer.getName());
          hmgr.insertSystemBillingAudit(invoicing, customer,
              "mainBilling.msg.zeroInvoice",
              "mainBilling.status.skipped");
          cIter.remove();
          skipped++;
          continue;
        }
      }
      if (newInvoice.getTotalPrice() < 0) {
        // skip customers with negative price invoices
        log.info("Skipping customer (NEGATIVE INVOICE): "
            + customer.getName());
        hmgr.insertSystemBillingAudit(invoicing, customer,
            "mainBilling.msg.negativeInvoice",
            "mainBilling.status.skipped");
        cIter.remove();
        skipped++;
        continue;
      }
      // wow we made it, customer has got an valid invoice, let's finalize
      // it
      invoices.add(newInvoice);
      // update customers billing info lastlyBilled
      customer.getBilling().setLastlyBilled(
          newInvoice.getPeriod().getTo());
      // remove customers ONE_TIME services
      sIter = customer.getServices().iterator();
      while (sIter.hasNext()) {
        Service service = sIter.next();
        if (Frequency.ONE_TIME.equals(service.getFrequency()))
          sIter.remove();
      }
      log.info("Customer INVOICED: " + customer.getName() + " ("
          + newInvoice.getNumber() + ")");
      nextInvoiceNo++;
    }
    invoicingLog = invoices.size() + "/" + skipped + "/" + errors + "/"
        + all;
    log.info("Invoicing customers (" + invoicingLog + ") FINISHED.");
    hmgr.insertSystemBillingAudit(invoicing, null,
        "mainBilling.msg.billingFinished", invoicingLog + " ***");
    return invoices;
  }

  public Bill generate(Customer customer, Date due, String number) {
    // get ivoice period for customer according to his lastly billed date
    Period invoicePeriod = getIvoicePeriod(customer.getBilling(), due);
    // no valid invoice period or no services means no invoice
    if ((invoicePeriod == null) || (customer.getServices().size() == 0))
      return null;
    // let's try to invoice the customer then
    Bill bill = new Bill();
    int realItemsCounter = 0;
    Date realFrom = invoicePeriod.getTo();
    Date realTo = invoicePeriod.getFrom();
    float itemAmount;
    Period itemPeriod = null;
    // add invoice items based on customer services
    for (Service service : customer.getServices()) {
      // skip services with zero service price as it will always produce
      // zero billFor line
      if (service.getPrice() == 0)
        continue;
      itemAmount = getBillItemAmount(invoicePeriod, service.getPeriod(),
          service.getFrequency());
      // skip services generating zero amount as periods does not overlap,
      // ONE_TIME will go throug
      if (itemAmount == 0)
        continue;
      // add the item to the invoice
      BillItem invoiceItem = new BillItem(service
          .getBillItemText(customer.getContact().getAddress()
              .getCountry()), itemAmount, service.getPrice());
      invoiceItem.setBill(bill);
      bill.getItems().add(invoiceItem);
      // update real invoice period sides and counter
      if (!Frequency.ONE_TIME.equals(service.getFrequency())) {
        // silently ignoring ONE_TIME services
        realItemsCounter++;
        itemPeriod = service.getPeriod().intersection(invoicePeriod);
        if (itemPeriod.getFrom().compareTo(realFrom) < 0)
          realFrom = itemPeriod.getFrom();
        if (itemPeriod.getTo().compareTo(realTo) > 0)
          realTo = itemPeriod.getTo();
      } else {
        // handle ONE_TIME services
        invoiceItem.setIsDisplayUnit(false);
      }
    }
    // no invoice items means no invoice, zero price invoices are considered
    // valid
    if (bill.getItems().size() == 0)
      return null;
    // let's finalize the invoice then
    bill.setNumber(number);
    // billFor.setHashCode(uuidGen.generateTimeBasedUUID() + "-" +
    // String.format("%h", customer.hashCode()));
    // create hash code from customer id and timestamp
    // NOTE: customer id has to be set! (persisted customer)
    bill.setHashCode(Long.toHexString(customer.getId() + 1000000)
        + Long.toHexString((new Date()).getTime()));
    bill.setInvoicedCustomer(customer);
    bill.setCustomerId(customer.getId());
    bill.setCustomerName(customer.getName());
    bill.setBillingDate(due);
    // set purge date using overloaded method (int), hibernate uses other
    // one (date)
    // ! needs to be called after setBillingDate()
    bill.setPurgeDate(sPurgeDateDays);
    // set invoice period
    if (realItemsCounter == 0)
      bill.setPeriod(invoicePeriod);
    else
      bill.setPeriod(new Period(realFrom, realTo));
    bill.setVat(sVat);
    // FIXME should not be so hardcoded
    if (Country.PL.equals(customer.getContact().getAddress().getCountry()))
      bill.setVat(VAT_PL);
    bill.setDeliverByMail(customer.getBilling().getDeliverByMail());
    return bill;
  }

  public float getBillItemAmount(Period invoicePeriod, Period servicePeriod,
                                 Frequency f) {
    // include one time services automaticaly
    if (f.equals(Frequency.ONE_TIME) && invoicePeriod.isComplete())
      return 1;
    // initial tests for correct periods
    if (!invoicePeriod.isComplete() || !servicePeriod.isValid()
        || servicePeriod.getFrom() == null)
      throw new IllegalArgumentException("Inconsistent periods!");
    // if got strange frequency get out of here,
    // be sure checking it AFTER ONE_TIME services are dealt with!
    if (f.getMonths() == 0)
      throw new IllegalArgumentException(
          "Unsupported service period! Can't bill it.");
    // get billing period and service period intersection
    Period intersection = invoicePeriod.intersection(servicePeriod);
    // no intersection, no amount
    if (intersection == null)
      return 0;
    // how much months (float) we have in intersection
    float monthsRate = intersection.getMonthsRate();
    // adjust amount to service.frequency
    float amount = f.getMonths() > 1 ? monthsRate / f.getMonths()
        : monthsRate;
    // round amount to 2 digits after comma
    log.debug("Item amount: " + amount);
    return (float) Math.round(amount * sPrecison) / sPrecison;
  }

  /**
   * @param b   Customer.billing
   * @param due date, the first of the due date month will be used (eg
   *            2007-03-17 => 2007-03-01)
   * @return period (from, to) for which customers services should be billed
   */
  public Period getIvoicePeriod(Billing b, Date due) {
    // fail fast
    if (b.getLastlyBilled() == null)
      throw new IllegalArgumentException("Billing.lastlyBilled not set.");
    if (due == null)
      throw new NullPointerException("No due date specified.");
    // init needed calendars
    Calendar cDue = new GregorianCalendar();
    cDue.setTime(due);
    Calendar cDueFirst = (Calendar) cDue.clone();
    // The first of the due date month is taken (eg 2007-03-17 =>
    // 2007-03-01)
    cDueFirst.set(Calendar.DAY_OF_MONTH, 1);
    Calendar cLastlyBilled = new GregorianCalendar();
    cLastlyBilled.setTime(b.getLastlyBilled());
    Calendar cFrom = (Calendar) cLastlyBilled.clone();
    // so add one day and you get first day of new billing period
    cFrom.add(Calendar.DAY_OF_MONTH, 1);
    Calendar cTo = null;
    // figure out billing period
    if (b.getIsBilledAfter()) {
      // compute period for billed after (past)
      cTo = (Calendar) cDueFirst.clone();
      // go to last of previous month
      cTo.add(Calendar.DAY_OF_MONTH, -1);
      // if to < from, unexisting period, get out of here with null
      if (cTo.compareTo(cFrom) < 0)
        return null;
      // is here anythink to billFor in this period according to billig
      // frequency
      if (((cTo.get(Calendar.MONTH) + 1) % b.getFrequency().getMonths()) != 0)
        return null;
    } else {
      // compute period for billed forward (future)
      cTo = (Calendar) cDueFirst.clone();
      cTo.add(Calendar.MONTH, b.getFrequency().getMonths());
      // go to last of previous month
      cTo.add(Calendar.DAY_OF_MONTH, -1);
      // if we have odd endings of q, qq, annual periods adjust it
      while (((cTo.get(Calendar.MONTH) + 1) % b.getFrequency()
          .getMonths()) != 0) {
        log.debug("Wrong endig of period!");
        cTo.add(Calendar.MONTH, -1);
        cTo.set(Calendar.DAY_OF_MONTH, cTo
            .getActualMaximum(Calendar.DAY_OF_MONTH));
      }
      // if to < from, unexisting period, get out of here with null
      if (cTo.compareTo(cFrom) < 0)
        return null;
    }
    return new Period(cFrom.getTime(), cTo.getTime());
  }

  public Bill send(Bill bill, MutableInt emailedCounter) {
    Customer c = dao.fetchCustomer(bill);
    Invoicing invoicing = getInvoicing(bill.getInvoicingId());
    log.info("Emailing bill for " + c.getName() + " (" + bill.getNumber()
        + ")");
    if (c == null)
      throw new IllegalArgumentException("Bill without customer set.");
    // email billFor
    if (c.getBilling().getDeliverByEmail()) {
      try {
        email(bill);
        // increment emailed bills counter
        if (emailedCounter != null)
          emailedCounter.setValue(emailedCounter.intValue() + 1);
      } catch (MailParseException e) {
        // billFor parse email error, autid it
        hmgr.insertSystemBillingAudit(invoicing, c,
            "mainBilling.msg.emailAddressError",
            "mainBilling.status.byMail");
        log.warn("Bill email parse exception: " + c.getName());
        log.warn(e.getCause());

        // set delivering by mail
        bill.setDeliverByMail(true);
      }
    }
    // set by snail mail if set
    if (c.getBilling().getDeliverByMail()) {
      bill.setDeliverByMail(true);
      // increment emailed bills counter if not already sent by email
      if (emailedCounter != null && !c.getBilling().getDeliverByEmail())
        emailedCounter.setValue(emailedCounter.intValue() + 1);
      log.info("Bill SENT by SNAIL MAIL for " + c.getName() + " ("
          + bill.getNumber() + ")");
    }
    // update billFor flags
    bill.setIsSent(true);
    bill.setIsDelivered(false);
    return bill;
  }

  public void reSendAll(Invoicing invoicing, List<Bill> bills) {
    if (invoicing != null)
      hmgr.insertSystemBillingAudit(invoicing, null,
          "mainBilling.msg.reSendingStarted", "******");
    // send all given bills
    int emailedCounter = 0;
    for (Bill bill : bills) {
      Customer customer = dao.fetchCustomer(bill);
      log.info("ReEmailing bill for " + customer.getName() + " ("
          + bill.getNumber() + ")");
      // email billFor
      if (customer.getBilling().getDeliverByEmail()) {
        try {
          emailReminder(bill);
          // increment emailed bills counter
          emailedCounter++;
        } catch (MailParseException e) {
          // billFor parse email error, autid it
          if (invoicing != null)
            hmgr.insertSystemBillingAudit(invoicing, customer,
                "mainBilling.msg.emailAddressError",
                "mainBilling.status.skipped");
          log.warn("Bill email parse exception: "
              + customer.getName());
        } catch (MailException e) {
          // billFor send email error, autid it
          if (invoicing != null)
            hmgr.insertSystemBillingAudit(invoicing, customer,
                "mainBilling.msg.emailSendingError",
                "mainBilling.status.skipped");
          log.warn("Bill email sending exception: "
              + customer.getName());
        }
        // due to anti-spam protection wait for certain time
        log.debug("Sleeping for " + emailSendingDelay + "s...");
        try {
          Thread.sleep(emailSendingDelay * 1000);
        } catch (InterruptedException e) {
          log.debug("Exception occured while sleeping: " + e);
        }
      }
    }
    if (invoicing != null)
      hmgr.insertSystemBillingAudit(invoicing, null,
          "mainBilling.msg.reSendingFinished", emailedCounter + "/"
              + bills.size() + " ***");
  }

  public void emailAll(List<Bill> bills) {
    // hmgr.insertSystemBillingAudit(null,
    // "mainBilling.msg.emailingStarted", "******");
    // send all given bills
    int emailedCounter = 0;
    for (Bill bill : bills) {
      Customer customer = dao.fetchCustomer(bill);
      log.info("Emailing bill for " + customer.getName() + " ("
          + bill.getNumber() + ")");
      // email billFor
      try {
        email(bill);
        // increment emailed bills counter
        emailedCounter++;
      } catch (MailParseException e) {
        // billFor parse email error, autid it
        // hmgr.insertSystemBillingAudit(customer,
        // "mainBilling.msg.emailAddressError",
        // "mainBilling.status.skipped");
        log.warn("Bill email parse exception: " + customer.getName());
      } catch (MailException e) {
        // billFor send email error, autid it
        // hmgr.insertSystemBillingAudit(customer,
        // "mainBilling.msg.emailSendingError",
        // "mainBilling.status.skipped");
        log.warn("Bill email sending exception: " + customer.getName());
      }
      // due to anti-spam protection wait for certain time
      log.debug("Sleeping for " + emailSendingDelay + "s...");
      try {
        Thread.sleep(emailSendingDelay * 1000);
      } catch (InterruptedException e) {
        log.debug("Exception occured while sleeping: " + e);
      }
    }
    // hmgr.insertSystemBillingAudit(null,
    // "mainBilling.msg.emailingFinished", emailedCounter + "/" +
    // bills.size() + " ***");
  }

  public void email(final Bill bill) throws MailException {
    if (mailSender == null)
      throw new IllegalStateException("JavaMailSender not set.");
    Invoice invoice = createNewInvoice(bill);
    MimeMessagePreparator messagePreparator = messagePreparatorFactory.newInstance(invoice);
    mailSender.send(messagePreparator);
    log.info("Email SENT for " + invoice.getEmail() + " (" + invoice.getNumber() + ")");
  }

  private Invoice createNewInvoice(final Bill bill) {
    final Customer customer = dao.fetchCustomer(bill);
    if (customer == null)
      throw new IllegalArgumentException("Bill without customer set.");
    return new Invoice(bill, customer);
  }

  private void emailReminder(final Bill bill) throws MailException {
    final Customer c = dao.fetchCustomer(bill);
    // handle PL customers as before
    if (Country.PL.equals(c.getContact().getAddress().getCountry())) {
      email(bill);
      return;
    }
    final Locale locale = c.getContact().getAddress().getCountry()
        .getLocale();
    if (c == null)
      throw new IllegalArgumentException("Bill without customer set.");
    if (mailSender == null)
      throw new IllegalStateException("JavaMailSender not set.");
    MimeMessagePreparator msgPreparator = new MimeMessagePreparator() {
      public void prepare(MimeMessage msg) throws MessagingException {
        MimeMessageHelper email = new MimeMessageHelper(msg);
        email.setFrom(emailFromAddressCs);
        email.setTo(c.getContact().getEmail());
        if (!GenericValidator.isBlankOrNull(c.getBilling()
            .getDeliverCopyEmail()))
          email
              .setCc(c.getBilling().getDeliverCopyEmail().split(
                  ","));
        email.setSentDate(new Date());
        email.getMimeMessage().addHeader("X-Priority", "1");
        email.getMimeMessage().addHeader("X-MSMail-Priority", "High");
        email.setSubject(MessagesUtils.getMessage(
            "billEmail.reminder.subject", new Object[]{
                bill.getNumber(),
                bill.getPeriod().getPeriodString()}, locale));
        StringBuffer text = new StringBuffer();
        text.append(MessagesUtils.getMessage(
            "billEmail.reminder.text.header", bill.getPeriod()
            .getPeriodString(), locale));
        text.append(MessagesUtils.getMessage(
            "billEmail.reminder.text.prefix", locale));
        text.append(MessagesUtils.getMessage("billEmail.text.link",
            bill.getHashCode(), locale));
        text.append(MessagesUtils.getMessage(
            "billEmail.reminder.text.suffix", locale));
        text.append(MessagesUtils.getMessage("billEmail.text.contact",
            locale));
        email.setText(text.toString());
      }
    };
    // execute send
    mailSender.send(msgPreparator);
    log.info("Email SENT for " + c.getName() + " (" + bill.getNumber()
        + ")");
  }

  public Bill confirmDelivery(String uuid) {
    Bill bill = null;
    try {
      bill = dao.get(uuid);
    } catch (ObjectRetrievalFailureException e) {
    }
    if (bill != null) {
      // we have billFor to confirm delivery
      bill.setIsSent(true);
      bill.setIsDelivered(true);
    }
    return bill;
  }

  public Customer fetchCustomer(Bill bill) {
    return dao.fetchCustomer(bill);
  }

  @SuppressWarnings("unchecked")
  public void invoiceAllCustomers(Invoicing invoicing, Date due,
                                  String invoiceNumberingBase) {
    // generate bills for all customers
    List<Bill> bills = generateAll(invoicing, cMgr.getAll(), due,
        invoiceNumberingBase);
    // for convenience preset bills confirmation
    for (Bill bill : bills)
      bill.setIsConfirmed(true);
    // persist generated bills
    insertAll(bills);
    // persist invoiced customers changes
    log
        .debug("Persisting invoiced customers changes (lastlyBilled, ONE_TIME services drop)");
    List<Customer> customers = (List<Customer>) CollectionUtils.collect(
        bills, new BeanToPropertyValueTransformer("invoicedCustomer"));
    cMgr.updateAll(customers);
  }

  public void sendNextInvoice() {
    Setting s = setMgr.get("billing.processSendingQueue.cz");
    if ((new Boolean(s.getValue()))) {
      Bill bill = dao.getToSend(Country.CZ);
      if (bill != null) {
        log.info("Sending next unsent CZ invoice to "
            + bill.getCustomerName() + " (" + bill.getNumber()
            + ")");
        send(bill, null);
        update(bill);
      } else { // when nothing to sent for specified country disable
        // sending
        s.setValue(new Boolean("false").toString());
        setMgr.update(s);
        log
            .info("Nothing to sent among CZ invoices, disabling automatic invoices sending for CZ");
      }
    }
    s = setMgr.get("billing.processSendingQueue.pl");
    if ((new Boolean(s.getValue()))) {
      Bill bill = dao.getToSend(Country.PL);
      if (bill != null) {
        log.info("Sending next unsent PL invoice to "
            + bill.getCustomerName() + " (" + bill.getNumber()
            + ")");
        send(bill, null);
        update(bill);
      } else { // when nothing to sent for specified country disable
        // sending
        s.setValue(new Boolean("false").toString());
        setMgr.update(s);
        log
            .info("Nothing to sent among PL invoices, disabling automatic invoices sending for PL");
      }
    }
  }

  public boolean getSendingEnabled(Country country) {
    Setting s = setMgr.get("billing.processSendingQueue."
        + country.getShortName());
    return (new Boolean(s.getValue())).booleanValue();
  }

  public void setSendingEnabled(boolean status, Country country) {
    Setting s = setMgr.get("billing.processSendingQueue."
        + country.getShortName());
    s.setValue((new Boolean(status).toString()));
    setMgr.update(s);
  }

  public int getCountByStatus(Invoicing invoicing, Boolean isConfirmed,
                              Boolean isSent, Boolean isDelivered, Boolean isArchived,
                              Boolean isSnail) {
    return dao.getCountByStatus(invoicing, isConfirmed, isSent,
        isDelivered, isArchived, isSnail);
  }

  @SuppressWarnings("unchecked")
  public void invoice(Invoicing invoicing) {
    // get customers to invoice
    Customer exampleCustomer = new Customer();
    exampleCustomer.setBilling(new Billing());
    exampleCustomer.getBilling().setFrequency(null);
    exampleCustomer.getBilling().setIsBilledAfter(null);
    // get active customers
    exampleCustomer.getBilling().setIsActive(true);
    exampleCustomer.getBilling().setStatus(null);
    exampleCustomer.setContact(new Contact());
    exampleCustomer.getContact().setAddress(new Address());
    // get customers from invocing country
    exampleCustomer.getContact().getAddress().setCountry(invoicing.getCountry());
    List<Customer> customersToInvoice = cMgr.getByExample(exampleCustomer);
    // generate bills for selected customers
    List<Bill> bills = generateAll(invoicing, customersToInvoice, invoicing
        .getInvoicingDate(), invoicing.getNumberingBase());
    // set bills confirmation and invoicing
    for (Bill bill : bills) {
      bill.setIsConfirmed(true);
      bill.setInvoicingId(invoicing.getId());
    }
    // persist generated bills
    insertAll(bills);
    // persist invoiced customers changes
    log
        .debug("Persisting invoiced customers changes (lastlyBilled, ONE_TIME services drop)");
    List<Customer> invoicedCustomers = (List<Customer>) CollectionUtils
        .collect(bills, new BeanToPropertyValueTransformer(
            "invoicedCustomer"));
    cMgr.updateAll(invoicedCustomers);
  }

  public List<Bill> getInvoices(Invoicing invoicing) {
    return dao.getByStatus(invoicing, null, null, null, null);
  }

  public List<Bill> getUndeliveredInvoices(Invoicing invoicing) {
    return dao.getByStatus(invoicing, true, true, false, false);
  }

  public List<Bill> getDeliveredInvoices(Invoicing invoicing) {
    return dao.getByStatus(invoicing, true, true, true, false);
  }

  public List<Bill> getForPrintingInvoices(Invoicing invoicing) {
    return dao.getBySentMail(invoicing);
  }

  public int getUndeliveredInvoicesCount(Invoicing invoicing) {
    return dao.getCountByStatus(invoicing, true, true, false, null, false);
  }

  public int getDeliveredInvoicesCount(Invoicing invoicing) {
    return dao.getCountByStatus(invoicing, true, true, true, null, null);
  }

  public int getForPrintingInvoicesCount(Invoicing invoicing) {
    return dao.getCountByStatus(invoicing, true, true, false, null, true);
  }

  public List<Invoicing> getInvoicings(Country country) {
    return dao.getInvoicings(country);
  }

  public Invoicing getInvoicing(Long id) {
    return dao.getInvoicing(id);
  }

  public void insertInvoicing(Invoicing invoicing) {
    // make sure we would insert
    invoicing.setId(null);
    // set new history id
    invoicing.setHistoryId(hmgr.getNewHistoryId());
    // store first history record for invoicing
    hmgr.insertSystemBillingAudit(invoicing, null,
        "mainBilling.msg.InvoicingCreated", invoicing.getName());
    // persist the invoicing
    dao.saveInvoicing(invoicing);
  }

  public void updateInvoicing(Invoicing invoicing) {
    // TODO we are not recording autdit!!!
    dao.saveInvoicing(invoicing);
  }

  public void deleteInvoicing(Invoicing invoicing) {
    // remove history first
    hmgr.deleteHistory(invoicing);
    dao.removeInvoicing(invoicing);
  }

  public int getInvoicingSum(Invoicing invoicing) {
    return dao.getInvoicingSum(invoicing);
  }

  public void exportAllToInsert(Invoicing invoicing, List<Bill> bills,
                                PrintWriter writer) {
    if (bills == null)
      return;
    if (writer == null)
      throw new IllegalArgumentException("Writer not initialized.");
    hmgr.insertSystemBillingAudit(invoicing, null,
        "mainBilling.msg.exportingStarted", "******");
    DateFormat dFmt = new SimpleDateFormat("yyyyMMddHHmmss");
    Locale locale = invoicing.getCountry().getLocale();
    // write header
    String timeStamp = dFmt.format(new Date());
    String exportBy = MessagesUtils.getMessage("billExport.by", locale);
    String infoLine = MessagesUtils.getMessage(
        "billExport.invoice.infoLine", new Object[]{timeStamp,
            exportBy}, locale);
    String spec1 = MessagesUtils.getMessage("billExport.invoice.spec1",
        locale);
    String spec2 = MessagesUtils.getMessage("billExport.invoice.spec2",
        locale);
    String specUnit = MessagesUtils.getMessage("billExport.item.unit",
        locale);
    // String specLinePrefix =
    // MessagesUtils.getMessage("billExport.item.servicePrefix", locale);
    String customerName = null;
    String customerLongName = null;
    writer.printf("[INFO]\r\n%s\r\n\r\n", infoLine);
    for (Iterator<Bill> iter = bills.iterator(); iter.hasNext();) {
      // append billFor to output writer
      Bill b = iter.next();
      Customer customer = dao.fetchCustomer(b);
      // invoice header
      writer.print("[NAGLOWEK]\r\n");
      // invoice number and symbol
      writer.printf("\"FS\",1,0,%s,,,\"%s\",,,,,\"%s\",", b
          .getNumberShortPL(), b.getNumberPL(), escapeQuotes(customer
          .getSymbol()));
      // customer address
      Address a = customer.getContact().getAddress();
      if (customer.getName().length() > 50) {
        customerName = customer.getName().substring(0, 47) + "...";
      } else {
        customerName = customer.getName();
      }
      if (customer.getSupplementaryName() != null
          && !"".equals(customer.getSupplementaryName())) {
        customerLongName = customer.getName() + " - "
            + customer.getSupplementaryName();
      } else {
        customerLongName = customer.getName();
      }
      writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",",
          escapeQuotes(customerName), escapeQuotes(customerLongName),
          a.getCity(), a.getPostalCode(), a.getStreet(), customer
          .getDIC());
      // dates
      String invoicingTimeStamp = dFmt.format(b.getBillingDate())
          .substring(0, 8)
          + "000000";
      writer.printf("%s,%s,%s,,%d,1,%s,", spec1, invoicingTimeStamp,
          invoicingTimeStamp, b.getItems().size(), spec2);
      // amounts
      writer.printf("%.4f,%.4f,%.4f,0.0000,,0.0000,,", new Float(b
          .getTotalPrice()), b.getBillVat(), b
          .getTotalPriceVatNotRounded());
      // summary
      writer
          .printf(
              "%s,0.0000,%.4f,0,0,1,0,\"%s\",,,0.0000,0.0000,\"PLN\",1.0000,,,,,0,0,0,,0.0000,,0.0000,\"Polska\",\"PL\",0",
              dFmt.format(b.getPurgeDate()), b
              .getTotalPriceVatNotRounded(), exportBy);
      writer.print("\r\n\r\n");
      // add invoice items, header first
      writer.print("[ZAWARTOSC]\r\n");
      int cnt = 0;
      String lineText = null;
      for (BillItem bi : b.getItems()) {
        // line prefix
        writer.printf(
            "%d,2,,1,0,0,1,0.0000,0.0000,%s,1.0000,1.0000,0.0000,",
            ++cnt, bi.getIsDisplayUnit() ? '"' + specUnit + '"'
                : "");
        float lineNet = new Float(bi.getLinePrice());
        // line amounts
        writer.printf("%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,0.0000,,",
            lineNet, bi.getLinePriceVat(), new Float(b.getVat()),
            lineNet, bi.getLineVat(), bi.getLinePriceVat());
        // line text
        if (bi.getIsDisplayUnit()) {
          // max 41 chars text + 9 chars period = 50 chars max
          lineText = (bi.getText().length() > 41 ? bi.getText()
              .substring(0, 38)
              + "..." : bi.getText())
              + ", " + b.getPeriod().getPeriodShortString();
        } else {
          // no period, get max 50 chars for text
          lineText = (bi.getText().length() > 50 ? bi.getText()
              .substring(0, 47)
              + "..." : bi.getText());
        }
        writer.printf("\"%s\"", lineText);
        writer.print("\r\n");
      }
      writer.print("\r\n");
    }
    hmgr.insertSystemBillingAudit(invoicing, null,
        "mainBilling.msg.exportingFinished", bills.size() + " ***");
  }

  private String escapeQuotes(String name) {
    return StringUtils.replace(name, "\"", "\"\"");
  }

  // should be transactional
  public void billCustomersIn(final Invoicing invoicing) {
    BillingContext context = billingContextFactory.billingContextFor(invoicing);
    Accountant accountant = newAccountantFor(invoicing, context);
    auditBillingStart(accountant);
    Iterable<Long> customers = customerDao.findActiveCustomerIdsByCountry(invoicing.getCountry());
    for (Long id : customers) {
      Customer customer = customerDao.get(id);
      BillingResult result = accountant.bill(customer);
      if (result.isSuccess())
        persistNewBIllAndUpdatedCustomerFrom(result);
      else
        auditBillBuildingErrors(invoicing, customer, result.errors());
    }
    auditBillingFinished(accountant);
  }

  protected Accountant newAccountantFor(final Invoicing invoicing, final BillingContext context) {
    return new Accountant(invoicing, context);
  }

  private void persistNewBIllAndUpdatedCustomerFrom(final BillingResult result) {
    dao.save(result.bill());
    customerDao.save(result.customer());
  }

  protected void auditBillBuildingErrors(final Invoicing invoicing, final Customer customer, final List<String> errors) {
    for (String error : errors) {
      String key = MESSAGE_KEYS_MAPPING.get(error);
      if (key == null)
        key = error;
      hmgr.insertSystemBillingAudit(invoicing, customer, key, "mainBilling.status.skipped");
    }
  }

  private static Map<String, String> errorsAndWarningsKeysMap() {
    HashMap<String, String> keys = new HashMap<String, String>();
    keys.put("billing.customerNotActive", "mainBilling.status.deactivated");
    keys.put("billing.billingDisabled", "mainBilling.msg.billinDisabled");
    keys.put("billing.customerHasNoServices", "mainBilling.msg.no-active-services");
    keys.put("billing.noBillForPeriod", "mainBilling.msg.invalidPeriod");
    keys.put("billing.noBillItems", "mainBilling.msg.no-active-services");
    keys.put("billing.zeroBillWithoutOneTimeItem", "mainBilling.msg.zeroInvoice");
    keys.put("billing.negativeAmountBill", "mainBilling.msg.negativeInvoice");
    keys.put("billing.error", "mainBilling.msg.illegalArgument");
    return Collections.unmodifiableMap(keys);
  }

  protected void auditBillingStart(final Accountant accountant) {
    String date = AUDIT_DATE_FORMAT.format(accountant.invoicing().getInvoicingDate());
    StringBuilder status = new StringBuilder(date);
    status.append(", ").append(accountant.invoicing().getNumberingBase()).append(" ***");
    hmgr.insertSystemBillingAudit(accountant.invoicing(), null,
        "mainBilling.msg.billingStarted", status.toString());
  }

  protected void auditBillingFinished(final Accountant accountant) {
    StringBuilder status = new StringBuilder();
    status.append(accountant.billedCount()).append("/").append(accountant.skippedCount()).append("/")
        .append(accountant.errorsCount()).append("/").append(accountant.processedCount()).append(" ***");
    hmgr.insertSystemBillingAudit(accountant.invoicing(), null,
        "mainBilling.msg.billingFinished", status.toString());
  }
  
}

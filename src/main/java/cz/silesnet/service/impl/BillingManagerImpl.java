package cz.silesnet.service.impl;

import com.google.common.base.Joiner;
import cz.silesnet.dao.BillDAO;
import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.model.*;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.invoice.Accountant;
import cz.silesnet.model.invoice.BillingContext;
import cz.silesnet.model.invoice.BillingContextFactory;
import cz.silesnet.model.invoice.BillingResult;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.SettingManager;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.mail.MimeMessagePreparatorFactory;
import cz.silesnet.util.MessagesUtils;
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

public class BillingManagerImpl implements BillingManager {
  private static final Map<String, String> MESSAGE_KEYS_MAPPING = errorsAndWarningsKeysMap();
  private static final SimpleDateFormat AUDIT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final String POHODA_REMINDERS_ENABLED = "pohoda-reminders-enabled";

    protected final Log log = LogFactory.getLog(getClass());

  private BillDAO dao;

  private CustomerDAO customerDao;

  private HistoryManager hmgr;

  private SettingManager setMgr;

  private MimeMessagePreparatorFactory messagePreparatorFactory;

  private JavaMailSender mailSender;

  private String emailFromAddressCs;

  private int emailSendingDelay = 5;

  private BillingContextFactory billingContextFactory;

    public void setReminderSenderFlag(final boolean flag) {
        Setting s = setMgr.get(POHODA_REMINDERS_ENABLED);
        s.setValue((new Boolean(flag).toString()));
        setMgr.update(s);
    }

    public boolean getReminderSenderFlag() {
        return Boolean.valueOf(setMgr.get(POHODA_REMINDERS_ENABLED).getValue());
    }

    public void billCustomersIn(final Invoicing invoicing) {
    BillingContext context = billingContextFactory.billingContextFor(invoicing.getCountry());
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
    final List<String> invoices = new ArrayList<>();
    for (Bill bill : result.bills()) {
      dao.save(bill);
      invoices.add(bill.getNumber());
    }
    customerDao.save(result.customer());
    final String invoiceNumbers = Joiner.on(", ").join(invoices);
    log.info("Customer " + result.customer().getName() + " successfully billed, bills [" + invoiceNumbers + "]");
  }

  protected void auditBillBuildingErrors(final Invoicing invoicing, final Customer customer, final List<String> errors) {
    for (String error : errors) {
      String key = MESSAGE_KEYS_MAPPING.get(error);
      if (key == null)
        key = error;
      hmgr.insertSystemBillingAudit(invoicing, customer, key, "mainBilling.status.skipped");
      log.info("Skipping customer [" + customer.getName() + "] " + error);
      // audit just firs error
      break;
    }
  }

  private static Map<String, String> errorsAndWarningsKeysMap() {
    HashMap<String, String> keys = new HashMap<String, String>();
    keys.put("billing.customerNotActive", "mainBilling.status.deactivated");
    keys.put("billing.invoicingCeased", "mainBilling.msg.invoicingCeased");
    keys.put("billing.customerDeadhead", "mainBilling.msg.customerDeadhead");
    keys.put("billing.customerHostsCell", "mainBilling.msg.customerHostsCell");
    keys.put("billing.customerVip", "mainBilling.msg.customerVip");
    keys.put("billing.customerPromotion", "mainBilling.msg.customerPromotion");
    keys.put("billing.billingExpired", "mainBilling.msg.billingExpired");
    keys.put("billing.customerGivenToJurist", "mainBilling.msg.customerGivenToJurist");
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
    status.append(", ").append(accountant.invoicing().getNumberingBase());
    String logStatus = status.toString();
    hmgr.insertSystemBillingAudit(accountant.invoicing(), null,
        "mainBilling.msg.billingStarted", status.append(" ***").toString());
    log.info("Billing [" + logStatus + "] STARTED...");
  }

  protected void auditBillingFinished(final Accountant accountant) {
    StringBuilder status = new StringBuilder();
    status.append(accountant.billedCount()).append("/").append(accountant.skippedCount()).append("/")
        .append(accountant.errorsCount()).append("/").append(accountant.processedCount());
    String logStatus = status.toString();
    hmgr.insertSystemBillingAudit(accountant.invoicing(), null,
        "mainBilling.msg.billingFinished", status.append(" ***").toString());
    log.info("Billing [" + logStatus + "] FINISHED.");
  }

  public Bill confirmDelivery(String uuid) {
    Bill bill = null;
    try {
      bill = dao.get(uuid);
    } catch (ObjectRetrievalFailureException e) { /* IGNORED */ }
    if (bill != null) {
      bill.setIsSent(true);
      bill.setIsDelivered(true);
    }
    return bill;
  }

  public void sendNextInvoice() {
    Setting s = setMgr.get("billing.processSendingQueue.cz");
    if ((Boolean.valueOf(s.getValue()))) {
      Bill bill = dao.getToSend(Country.CZ);
      if (bill != null) {
        log.info("Sending next unsent CZ invoice to " + bill.getCustomerName() + " (" + bill.getNumber() + ")");
        send(bill, null);
        update(bill);
      } else { // when nothing to sent for specified country disable sending
        s.setValue(Boolean.FALSE.toString());
        setMgr.update(s);
        log.info("Nothing to sent among CZ invoices, disabling automatic invoices sending for CZ");
      }
    }
    s = setMgr.get("billing.processSendingQueue.pl");
    if ((Boolean.valueOf(s.getValue()))) {
      Bill bill = dao.getToSend(Country.PL);
      if (bill != null) {
        log.info("Sending next unsent PL invoice to " + bill.getCustomerName() + " (" + bill.getNumber() + ")");
        send(bill, null);
        update(bill);
      } else { // when nothing to sent for specified country disable sending
        s.setValue(Boolean.FALSE.toString());
        setMgr.update(s);
        log.info("Nothing to sent among PL invoices, disabling automatic invoices sending for PL");
      }
    }
  }

  public boolean getSendingEnabled(Country country) {
    Setting s = setMgr.get("billing.processSendingQueue." + country.getShortName());
    return (new Boolean(s.getValue())).booleanValue();
  }

  public void setSendingEnabled(boolean status, Country country) {
    Setting s = setMgr.get("billing.processSendingQueue." + country.getShortName());
    s.setValue((new Boolean(status).toString()));
    setMgr.update(s);
  }

  public void exportAllToInsert(Invoicing invoicing, List<Bill> bills, PrintWriter writer) {
    if (bills == null)
      return;
    if (writer == null)
      throw new IllegalArgumentException("Writer not initialized.");
    hmgr.insertSystemBillingAudit(invoicing, null, "mainBilling.msg.exportingStarted", "******");
    DateFormat dFmt = new SimpleDateFormat("yyyyMMddHHmmss");
    Locale locale = invoicing.getCountry().getLocale();
    // write header
    String timeStamp = dFmt.format(new Date());
    String exportBy = MessagesUtils.getMessage("billExport.by", locale);
    String infoLine = MessagesUtils.getMessage("billExport.invoice.infoLine",
        new Object[]{timeStamp, exportBy}, locale);
    String spec1 = MessagesUtils.getMessage("billExport.invoice.spec1", locale);
    String spec2 = MessagesUtils.getMessage("billExport.invoice.spec2", locale);
    String specUnit = MessagesUtils.getMessage("billExport.item.unit", locale);
    String customerName;
    String customerLongName;
    writer.printf("[INFO]\r\n%s\r\n\r\n", infoLine);
    for (Iterator<Bill> iter = bills.iterator(); iter.hasNext(); ) {
      // append billFor to output writer
      Bill b = iter.next();
      Customer customer = dao.fetchCustomer(b);
      // invoice header
      writer.print("[NAGLOWEK]\r\n");
      // invoice number and symbol
      writer.printf("\"FS\",1,0,%s,,,\"%s\",,,,,\"%s\",", b.getNumberShortPL(), b.getNumberPL(),
          escapeQuotes(customer.getSymbol()));
      // customer address
      Address a = customer.getContact().getAddress();
      if (customer.getName().length() > 50) {
        customerName = customer.getName().substring(0, 47) + "...";
      } else {
        customerName = customer.getName();
      }
      if (customer.getSupplementaryName() != null && !"".equals(customer.getSupplementaryName())) {
        customerLongName = customer.getName() + " - " + customer.getSupplementaryName();
      } else {
        customerLongName = customer.getName();
      }
      writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",", escapeQuotes(customerName),
          escapeQuotes(customerLongName), a.getCity(), a.getPostalCode(), a.getStreet(), customer.getDIC());
      // dates
      String invoicingTimeStamp = dFmt.format(b.getBillingDate()).substring(0, 8) + "000000";
      writer.printf("%s,%s,%s,,%d,1,%s,", spec1, invoicingTimeStamp, invoicingTimeStamp,
          b.getItems().size(), spec2);
      // amounts
      writer.printf("%.4f,%.4f,%.4f,0.0000,,0.0000,,", new Float(b.getTotalPrice()), b.getBillVat(),
          b.getTotalPriceVatNotRounded());
      // summary
      writer.printf("%s,0.0000,%.4f,0,0,1,0,\"%s\",,,0.0000,0.0000,\"PLN\",1.0000,,,,,0,0,0,,0.0000,,0.0000,\"Polska\",\"PL\",0",
          dFmt.format(b.getPurgeDate()), b.getTotalPriceVatNotRounded(), exportBy);
      writer.print("\r\n\r\n");
      // add invoice items, header first
      writer.print("[ZAWARTOSC]\r\n");
      int cnt = 0;
      String lineText = null;
      for (BillItem bi : b.getItems()) {
        // line prefix
        writer.printf("%d,2,,1,0,0,1,0.0000,0.0000,%s,1.0000,1.0000,0.0000,",
            ++cnt, bi.getIsDisplayUnit() ? '"' + specUnit + '"' : "");
        float lineNet = new Float(bi.getLinePrice());
        // line amounts
        writer.printf("%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,0.0000,,", lineNet, bi.getLinePriceVat(),
            new Float(b.getVat()), lineNet, bi.getLineVat(), bi.getLinePriceVat());
        // line text
        if (bi.getIsDisplayUnit()) {
          // max 41 chars text + 9 chars period = 50 chars max
          lineText = (bi.getText().length() > 41 ?
              bi.getText().substring(0, 38) + "..." :
              bi.getText()) + ", " + b.getPeriod().getPeriodShortString();
        } else {
          // no period, get max 50 chars for text
          lineText = (bi.getText().length() > 50 ?
              bi.getText().substring(0, 47) + "..." :
              bi.getText());
        }
        writer.printf("\"%s\"", lineText);
        writer.print("\r\n");
      }
      writer.print("\r\n");
    }
    hmgr.insertSystemBillingAudit(invoicing, null, "mainBilling.msg.exportingFinished", bills.size() + " ***");
  }

  private String escapeQuotes(String name) {
    return StringUtils.replace(name, "\"", "\"\"");
  }

  public Bill send(Bill bill, MutableInt emailedCounter) {
    Customer c = dao.fetchCustomer(bill);
    Invoicing invoicing = getInvoicing(bill.getInvoicingId());
    log.info("Emailing bill for " + c.getName() + " (" + bill.getNumber() + ")");
    if (c == null)
      throw new IllegalArgumentException("Bill without customer set.");
    if (c.getBilling().getDeliverByEmail()) {
      try {
        email(bill);
        if (emailedCounter != null)
          emailedCounter.setValue(emailedCounter.intValue() + 1);
      } catch (MailException e) {
        hmgr.insertSystemBillingAudit(invoicing, c, "mainBilling.msg.emailAddressError", "mainBilling.status.byMail");
        log.warn("Bill email exception: " + c.getName());
        log.warn(e.getCause());
        bill.setDeliverByMail(true);
      }
    }
    // set by snail mail if set
    if (c.getBilling().getDeliverByMail()) {
      bill.setDeliverByMail(true);
      // increment emailed bills counter if not already sent by email
      if (emailedCounter != null && !c.getBilling().getDeliverByEmail())
        emailedCounter.setValue(emailedCounter.intValue() + 1);
      log.info("Bill SENT by SNAIL MAIL for " + c.getName() + " (" + bill.getNumber() + ")");
    }
    // update billFor flags
    bill.setIsSent(true);
    bill.setIsDelivered(false);
    return bill;
  }

  public void reSendAll(Invoicing invoicing, List<Bill> bills) {
    if (invoicing != null)
      hmgr.insertSystemBillingAudit(invoicing, null, "mainBilling.msg.reSendingStarted", "******");
    int emailedCounter = 0;
    for (Bill bill : bills) {
      Customer customer = dao.fetchCustomer(bill);
      log.info("ReEmailing bill for " + customer.getName() + " (" + bill.getNumber() + ")");
      if (customer.getBilling().getDeliverByEmail()) {
        try {
          emailReminder(bill);
          emailedCounter++;
        } catch (MailParseException e) {
          if (invoicing != null)
            hmgr.insertSystemBillingAudit(invoicing, customer, "mainBilling.msg.emailAddressError", "mainBilling.status.skipped");
          log.warn("Bill email parse exception: " + customer.getName());
        } catch (MailException e) {
          if (invoicing != null)
            hmgr.insertSystemBillingAudit(invoicing, customer, "mainBilling.msg.emailSendingError", "mainBilling.status.skipped");
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
    }
    if (invoicing != null)
      hmgr.insertSystemBillingAudit(invoicing, null, "mainBilling.msg.reSendingFinished", emailedCounter + "/" + bills.size() + " ***");
  }

  public void emailAll(List<Bill> bills) {
    for (Bill bill : bills) {
      Customer customer = dao.fetchCustomer(bill);
      log.info("Emailing bill for " + customer.getName() + " (" + bill.getNumber() + ")");
      try {
        email(bill);
      } catch (MailParseException e) {
        log.warn("Bill email parse exception: " + customer.getName());
      } catch (MailException e) {
        log.warn("Bill email sending exception: " + customer.getName());
      }
      // due to anti-spam protection wait for certain time
      log.debug("Sleeping for " + emailSendingDelay + "s...");
      try {
        Thread.sleep(emailSendingDelay * 1000);
      } catch (InterruptedException e) {
        log.debug("Exception occurred while sleeping: " + e);
      }
    }
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
    if (c == null)
      throw new IllegalArgumentException("Bill without customer set.");
    // handle PL customers as before
    if (Country.PL.equals(c.getContact().getAddress().getCountry())) {
      email(bill);
      return;
    }
    final Locale locale = c.getContact().getAddress().getCountry().getLocale();
    if (mailSender == null)
      throw new IllegalStateException("JavaMailSender not set.");
    MimeMessagePreparator msgPreparator = new MimeMessagePreparator() {
      public void prepare(MimeMessage msg) throws MessagingException {
        MimeMessageHelper email = new MimeMessageHelper(msg);
        email.setFrom(emailFromAddressCs);
        email.setTo(c.getContact().getEmail());
        if (!GenericValidator.isBlankOrNull(c.getBilling().getDeliverCopyEmail()))
          email.setCc(c.getBilling().getDeliverCopyEmail().split(","));
        email.setSentDate(new Date());
        email.getMimeMessage().addHeader("X-Priority", "1");
        email.getMimeMessage().addHeader("X-MSMail-Priority", "High");
        email.setSubject(MessagesUtils.getMessage("billEmail.reminder.subject",
            new Object[]{bill.getNumber(), bill.getPeriod().getPeriodString()}, locale));
        StringBuffer text = new StringBuffer();
        text.append(MessagesUtils.getMessage("billEmail.reminder.text.header",
            bill.getPeriod().getPeriodString(), locale));
        text.append(MessagesUtils.getMessage("billEmail.reminder.text.prefix", locale));
        text.append(MessagesUtils.getMessage("billEmail.text.link", bill.getHashCode(), locale));
        text.append(MessagesUtils.getMessage("billEmail.reminder.text.suffix", locale));
        text.append(MessagesUtils.getMessage("billEmail.text.contact", locale));
        email.setText(text.toString());
      }
    };
    // execute send
    mailSender.send(msgPreparator);
    log.info("Email SENT for " + c.getName() + " (" + bill.getNumber() + ")");
  }

  // bill repository methods

  public Bill get(Long billId) {
    return dao.get(billId);
  }

  public void insert(Bill bill) {
    dao.save(bill);
  }

  public void update(Bill bill) {
    dao.save(bill);
  }

  public List<Bill> getByStatus(Invoicing invoicing, Boolean isConfirmed, Boolean isSent,
                                Boolean isDelivered, Boolean isArchived) {
    return dao.getByStatus(invoicing, isConfirmed, isSent, isDelivered, isArchived);
  }

  public int getCountByStatus(Invoicing invoicing, Boolean isConfirmed, Boolean isSent,
                              Boolean isDelivered, Boolean isArchived, Boolean isSnail) {
    return dao.getCountByStatus(invoicing, isConfirmed, isSent, isDelivered, isArchived, isSnail);
  }

  public List<Bill> getByCustomer(Customer c) {
    return dao.getByCustomer(c);
  }

  public List<Bill> getBySentMail(Invoicing invoicing) {
    return dao.getBySentMail(invoicing);
  }

  public List<Bill> getByExample(Bill bill) {
    return dao.getByExample(bill);
  }

  public Customer fetchCustomer(Bill bill) {
    return dao.fetchCustomer(bill);
  }

  // invoicing repository methods

  public Invoicing getInvoicing(Long id) {
    return dao.getInvoicing(id);
  }

  public void insertInvoicing(Invoicing invoicing) {
    invoicing.setId(null); // make sure we would insert
    invoicing.setHistoryId(hmgr.getNewHistoryId());
    hmgr.insertSystemBillingAudit(invoicing, null, "mainBilling.msg.InvoicingCreated", invoicing.getName());
    dao.saveInvoicing(invoicing);
  }

  public List<Bill> getInvoices(Invoicing invoicing) {
    return dao.getByStatus(invoicing, null, null, null, null);
  }

  public List<Invoicing> getInvoicings(Country country) {
    return dao.getInvoicings(country);
  }

  public int getInvoicingSum(Invoicing invoicing) {
    return dao.getInvoicingSum(invoicing);
  }

  // Setters

  public void setBillingContextFactory(final BillingContextFactory billingContextFactory) {
    this.billingContextFactory = billingContextFactory;
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

}

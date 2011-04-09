package cz.silesnet.model;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Entity clas holding customers billFor.
 *
 * @author Richard Sikora
 */
public class Bill extends Entity {

  // ~ Static fields/initializers
  // ---------------------------------------------

  private static final long serialVersionUID = -1741749406722866169L;

  // ~ Instance fields
  // --------------------------------------------------------

  private String fNumber;

  private Date fBillingDate;

  private Date fPurgeDate;

  private Customer fInvoicedCustomer;

  private Long fCustomerId;

  private Long fInvoicingId;

  private String fCustomerName;

  private Period fPeriod;

  private List<BillItem> fItems = new ArrayList<BillItem>();

  private int fVat = 19;

  private String fHashCode;

  private Boolean fIsConfirmed = false;

  private Boolean fIsSent = false;

  private Boolean fIsDelivered = false;

  private Boolean fIsArchived = false;

  private Boolean fDeliverByMail = false;

  // ~ Methods
  // ----------------------------------------------------------------

  public Customer getInvoicedCustomer() {
    return fInvoicedCustomer;
  }

  public void setInvoicedCustomer(Customer invoicedCustomer) {
    fInvoicedCustomer = invoicedCustomer;
    if (invoicedCustomer != null) {
      fCustomerId = invoicedCustomer.getId();
      fCustomerName = invoicedCustomer.getName();
    }
  }

  public void setHashCode(String hashCode) {
    fHashCode = hashCode;
  }

  public String getHashCode() {
    return fHashCode;
  }

  public void setIsConfirmed(Boolean isConfirmed) {
    fIsConfirmed = isConfirmed;
  }

  public Boolean getIsConfirmed() {
    return fIsConfirmed;
  }

  public void setIsDelivered(Boolean isDelivered) {
    fIsDelivered = isDelivered;
  }

  public Boolean getIsDelivered() {
    return fIsDelivered;
  }

  public void setIsSent(Boolean isSent) {
    fIsSent = isSent;
  }

  public Boolean getIsSent() {
    return fIsSent;
  }

  public void setItems(List<BillItem> items) {
    fItems = items;
  }

  public List<BillItem> getItems() {
    return fItems;
  }

  public void setPeriod(Period period) {
    fPeriod = period;
  }

  public Period getPeriod() {
    return fPeriod;
  }

  public int getTotalPrice() {
    if (fItems == null)
      return 0;
    int total = 0;
    for (BillItem item : fItems)
      total += item.getLinePrice();
    return total;
  }

  public float getBillVat() {
    return (float) (getTotalPrice() * getVat()) / 100;
  }

  public float getBillRoundedVat() {
    // billFor VAT is roundet up to 2 digits afrer coma by definition
    // round it to 50 cents mathematicaly
    return (float) Math.round(getBillVat() * 2) / 2;
  }

  public float getTotalPriceVat() {
    return getTotalPrice() + getBillRoundedVat();
  }

  public float getTotalPriceVatNotRounded() {
    return getTotalPrice() + getBillVat();
  }

  public Boolean getIsArchived() {
    return fIsArchived;
  }

  public void setIsArchived(Boolean isArchived) {
    fIsArchived = isArchived;
  }

  public String getNumber() {
    return fNumber;
  }

  public String getNumberShortPL() {
    return StringUtils.stripStart(fNumber.substring(4), "0");
  }

  public String getNumberPL() {
    return getNumberShortPL() + "/" + fNumber.substring(0, 4);
  }

  public void setNumber(String number) {
    fNumber = number;
  }

  public Date getBillingDate() {
    return fBillingDate;
  }

  public void setBillingDate(Date billingDate) {
    fBillingDate = billingDate;
  }

  public void setPurgeDate(int purgeDateDays) {
    if (getBillingDate() != null) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(getBillingDate());
      cal.add(Calendar.DAY_OF_MONTH, purgeDateDays);
      setPurgeDate(cal.getTime());
    } else
      setPurgeDate(null);
  }

  public int getVat() {
    return fVat;
  }

  public void setVat(int vat) {
    fVat = vat;
  }

  public Date getPurgeDate() {
    return fPurgeDate;
  }

  public void setPurgeDate(Date purgeDate) {
    fPurgeDate = purgeDate;
  }

  public Boolean getDeliverByMail() {
    return fDeliverByMail;
  }

  public void setDeliverByMail(Boolean deliverByMail) {
    fDeliverByMail = deliverByMail;
  }

  public String getCustomerName() {
    return fCustomerName;
  }

  public void setCustomerName(String customerName) {
    fCustomerName = customerName;
  }

  public Long getCustomerId() {
    return fCustomerId;
  }

  public void setCustomerId(Long customerId) {
    fCustomerId = customerId;
  }

  public Long getInvoicingId() {
    return fInvoicingId;
  }

  public void setInvoicingId(Long invoicingId) {
    fInvoicingId = invoicingId;
  }

  public float getNet() {
    if (fItems == null)
      return 0.0F;
    double net = 0.0;
    for (BillItem item : fItems)
      net += item.getNet();
    return (float) net;
  }

  public int getNetRounded() {
    return Math.round(getNet());
  }

  public int getVatRounded() {
    double vat = getNetRounded() * (fVat / 100.0);
    return (int) Math.round(vat);
  }

  public int getBrt() {
    return getNetRounded() + getVatRounded();
  }

}
package cz.silesnet.model;

import cz.silesnet.model.enums.Country;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Invoicing extends Entity implements Historic {

  /**
   *
   */
  private static final long serialVersionUID = -6364779012064785211L;

  // FIXME should do it more elegant!!!
  // it depends on id value in db !!!
  private static final Long sHistoryTypeLabelId = Long.valueOf(43);

  private static final String[] sDiffExcludeFields = {"class$0", "serialVersionUID", "sHistoryTypeLabelId", "sDiffExcludeFields", "fId", "fHistoryId"};

  private Long historyId;

  private String name;

  private Country country;

  private Date invoicingDate;

  private String numberingBase;

  public void setHistoryId(Long historyId) {
    this.historyId = historyId;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNumberingBase() {
    return numberingBase;
  }

  public void setNumberingBase(String numberingBase) {
    if (this.numberingBase != null)
      throw new IllegalStateException("numbering base already set");
    this.numberingBase = numberingBase;
  }

  public Date getInvoicingDate() {
    return invoicingDate;
  }

  public void setInvoicingDate(Date invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  public Invoicing() {
    super();
  }

  public String[] getDiffExcludeFields() {
    return sDiffExcludeFields;
  }

  public Long getHistoryId() {
    return historyId;
  }

  public Long getHistoryTypeLabelId() {
    return sHistoryTypeLabelId;
  }

  public String getProposedName() {
    if (country == null || invoicingDate == null)
      throw new IllegalStateException("Country or invoicing date not set!");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
    return country.getShortName().toUpperCase() + " " + dateFormat.format(invoicingDate);
  }

}

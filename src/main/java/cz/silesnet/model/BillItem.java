package cz.silesnet.model;

import cz.silesnet.model.invoice.Amount;
import cz.silesnet.model.invoice.Percent;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Bill item entity object.
 *
 * @author Richard Sikora
 */
public class BillItem implements Serializable {

  // ~ Instance fields
  // --------------------------------------------------------

  private static final long serialVersionUID = 4071067933886947302L;

  private Bill fBill;

  private String fText;

  private float fAmount;

  private int fPrice;

  private Boolean fIsDisplayUnit = true;

  private Boolean fIncludeDph = true;

  private Charge charge;

  // ~ Constructors
  // -----------------------------------------------------------

  public BillItem() {
    super();
  }

  public BillItem(String text, float amount, int price) {
    super();
    fText = text;
    fAmount = amount;
    fPrice = price;
  }

  public BillItem(final String name, final Percent percent, final Amount price) {
    this(name, percent.toBigDecimal().floatValue(), price.value().intValue());
  }

  // ~ Methods
  // ----------------------------------------------------------------

  public void setPrice(int price) {
    fPrice = price;
  }

  public int getPrice() {
    return fPrice;
  }

  public void setText(String text) {
    fText = text;
  }

  public String getText() {
    return fText;
  }

  public int getLinePrice() {
    return Math.round(fAmount * fPrice);
  }

  public float getLinePriceVat() {
    if (getBill() == null)
      throw new IllegalStateException(
          "Bill item without bill, can not get VAT value.");
    return (float) (getLinePrice() * (100 + getBill().getVat())) / 100;
  }

  public float getLineVat() {
    return getLinePriceVat() - getLinePrice();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this,
        ToStringStyle.MULTI_LINE_STYLE);
  }

  public Float getAmount() {
    return fAmount;
  }

  public void setAmount(float amount) {
    fAmount = amount;
  }

  public Bill getBill() {
    return fBill;
  }

  public void setBill(Bill bill) {
    fBill = bill;
  }

  public Boolean getIsDisplayUnit() {
    return fIsDisplayUnit;
  }

  public void setIsDisplayUnit(Boolean isDisplayUnit) {
    fIsDisplayUnit = isDisplayUnit;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public float getNet() {
    return fAmount * fPrice;
  }

  public float getVat() {
    if (getBill() == null)
      throw new IllegalStateException(
          "Bill item without bill, can not get VAT value.");
    if (!fIncludeDph) {
      return 0;
    }
    double vat = getNet() * (getBill().getVat() / 100.0);
    return (float) vat;
  }

  public float getBrt() {
    return getNet() + getVat();
  }

  public Amount net() {
    return Amount.of(getNet());
  }

  public Boolean getIncludeDph() {
    return fIncludeDph;
  }

  public void setIncludeDph(Boolean fIncludeDph) {
    this.fIncludeDph = fIncludeDph;
  }

  public int getVatRate() {
    return fIncludeDph ? fBill.getVat() : 0;
  }

  public Charge getCharge() {
    if (charge == null) {
      charge = Charge.of(this.fAmount, this.fPrice, (fIncludeDph && fBill != null ? fBill.getVat() : 0));
    }
    return charge;
  }
}

package cz.silesnet.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * Bill item entity object.
 *
 * @author Richard Sikora
 */
public class BillItem implements Serializable {

    //~ Instance fields --------------------------------------------------------

	private static final long serialVersionUID = 4071067933886947302L;
    private Bill fBill;
	private String  fText;
    private float fAmount;
    private int fPrice;
    private Boolean fIsDisplayUnit = true;

    //~ Constructors -----------------------------------------------------------
    public BillItem() {
    	super();
    }

    public BillItem(String text, float amount, int price) {
    	super();
    	fText       = text;
        fAmount     = amount;
        fPrice      = price;
    }

    //~ Methods ----------------------------------------------------------------


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
			throw new IllegalStateException("Bill item without bill, can not get VAT value.");
		return (float) (getLinePrice() * (100 + getBill().getVat())) / 100;
	}

	public float getLineVat() {
		return getLinePriceVat() - getLinePrice();
	}
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
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
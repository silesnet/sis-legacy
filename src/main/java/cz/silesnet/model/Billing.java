package cz.silesnet.model;

import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.model.enums.InvoiceFormat;
import cz.silesnet.util.Dates;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Component class for customer billing.
 *
 * @author Richard Sikora
 */
public class Billing implements HistoricToString, Serializable {
    private static final long serialVersionUID = -946891192465278665L;

    private static DateFormat sDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Frequency fFrequency = Frequency.MONTHLY;

    private Date fLastlyBilled;

    private Boolean fIsBilledAfter = false;

    private Boolean fDeliverByMail = false;

    private Boolean fDeliverByEmail = true;

    private String fDeliverCopyEmail;

    private InvoiceFormat format = InvoiceFormat.LINK;

    private Boolean deliverSigned = false;

    private Boolean fIsActive = true;

    private BillingStatus fStatus = BillingStatus.INVOICE;

    private String fAccountNumber;

    private String fBankCode;

    private Integer fVariableSymbol;

    public void setFrequency(Frequency frequency) {
        fFrequency = frequency;
    }

    public Frequency getFrequency() {
        return fFrequency;
    }

    public String getHistoricToString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append(getLastlyBilled() != null ? sDateFormat.format(getLastlyBilled()) : "")
                .append(getFrequency())
                .append(getIsBilledAfter())
                .append(getDeliverByMail())
                .append(getDeliverByEmail())
                .append(getDeliverCopyEmail())
                .append(getFormat().toString())
                .append(getDeliverSigned())
                .append(getIsActive())
                .append(getStatus())
                .toString();
    }

    public void setIsBilledAfter(Boolean isBilledAfter) {
        fIsBilledAfter = isBilledAfter;
    }

    public Boolean getIsBilledAfter() {
        return fIsBilledAfter;
    }

    public void setLastlyBilled(Date lastlyBilled) {
        fLastlyBilled = lastlyBilled;
    }

    public Date getLastlyBilled() {
        return fLastlyBilled;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public Boolean getDeliverByEmail() {
        return fDeliverByEmail;
    }

    public void setDeliverByEmail(Boolean deliverByEmail) {
        fDeliverByEmail = deliverByEmail;
    }

    public Boolean getDeliverByMail() {
        return fDeliverByMail;
    }

    public void setDeliverByMail(Boolean deliverByMail) {
        fDeliverByMail = deliverByMail;
    }

    public String getDeliverCopyEmail() {
        return fDeliverCopyEmail != null ? fDeliverCopyEmail.replace(" ", "")
                : null;
    }

    public void setDeliverCopyEmail(String deliverCopyEmail) {
        fDeliverCopyEmail = deliverCopyEmail != null ? deliverCopyEmail
                .replace(" ", "") : null;
    }

    public Boolean getIsActive() {
        return fIsActive;
    }

    public void setIsActive(Boolean isActive) {
        fIsActive = isActive;
    }

    public BillingStatus getStatus() {
        return fStatus;
    }

    public void setStatus(BillingStatus status) {
        fStatus = status;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Boolean getDeliverSigned() {
        return deliverSigned;
    }

    public void setDeliverSigned(Boolean deliverSigned) {
        this.deliverSigned = deliverSigned;
    }

    public InvoiceFormat getFormat() {
        return format;
    }

    public void setFormat(InvoiceFormat format) {
        this.format = format;
    }

    public String getAccountNumber() {
        return fAccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        fAccountNumber = accountNumber;
    }

    public String getBankCode() {
        return fBankCode;
    }

    public void setBankCode(String bankCode) {
        fBankCode = bankCode;
    }

    public String getBankAccount() {
        if (StringUtils.isNotBlank(fAccountNumber)) {
            StringBuilder number = new StringBuilder(fAccountNumber);
            if (StringUtils.isNotBlank(fBankCode))
                number.append("/").append(fBankCode);
            return number.toString();
        } else {
            return "";
        }
    }

    public Period nextBillPeriod(Date due) {
        if (getLastlyBilled() == null)
            return Period.NONE;
        Calendar from = nextInvoiceFrom();
        Calendar to = nextInvoiceTo(due);
        Period period = new Period(from.getTime(), to.getTime());
        return period.isCompleteAndValid() ? period : Period.NONE;
    }

    protected Calendar nextInvoiceFrom() {
        Calendar invoiceFrom = Dates.calendarWithZeroTimeFrom(getLastlyBilled());
        invoiceFrom.add(Calendar.DAY_OF_MONTH, 1);
        return invoiceFrom;
    }

    protected Calendar nextInvoiceTo(Date due) {
        Period frequencyPeriod = getFrequency().periodFor(due);
        Calendar invoiceTo;
        if (getIsBilledAfter()) {
            invoiceTo = Dates.calendarFor(frequencyPeriod.getFrom());
            invoiceTo.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            invoiceTo = Dates.calendarFor(frequencyPeriod.getTo());
        }
        return invoiceTo;
    }

    public Integer getVariableSymbol() {
        return fVariableSymbol;
    }

    public void setVariableSymbol(Integer fVariableSymbol) {
        this.fVariableSymbol = fVariableSymbol;
    }
}

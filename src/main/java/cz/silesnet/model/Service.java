package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static cz.silesnet.model.ServiceId.serviceId;

/**
 * Entity class to hold services served to customers.
 *
 * @author Richard Sikora
 */
public class Service extends Entity implements HistoricToString {

    // ~ Static fields/initializers
    // ---------------------------------------------

    private static final long serialVersionUID = 8334678382856809902L;

    // ~ Instance fields
    // --------------------------------------------------------

    private Long fCustomerId;

    private Period fPeriod = new Period();

    private String fName;

    private String additionalName;

    private Integer fPrice;

    private Frequency fFrequency = Frequency.MONTHLY;

    private String fInfo;

    private Integer fContract;

    private String fStatus = "INHERIT_FROM_CUSTOMER";

    private Integer fAddressId;

    private Boolean fIncludeDph = true;

    private Integer fProductId;

    // ~ Methods
    // ----------------------------------------------------------------

    public void setCustomerId(Long customerId) {
        fCustomerId = customerId;
    }

    public Long getCustomerId() {
        return fCustomerId;
    }

    public void setFrequency(Frequency frequency) {
        fFrequency = frequency;
    }

    public Frequency getFrequency() {
        return fFrequency;
    }

    public String getHistoricToString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getShortInfo()).append(
                (getPeriod() != null) ? getPeriod().getHistoricToString() : null).append(getFrequency())
                .append(getPrice()).append(getInfo()).toString();
    }

    public void setInfo(String info) {
        fInfo = info;
    }

    public String getInfo() {
        return fInfo;
    }

    public void setName(String name) {
        // FIXME dirty workaround
        if ("0".equals(name))
            fName = "";
        else
            fName = name;
    }

    public String getName() {
        return fName;
    }

    public void setPeriod(Period period) {
        fPeriod = period;
    }

    public Period getPeriod() {
        return fPeriod;
    }

    public void setPrice(Integer price) {
        fPrice = price;
    }

    public Integer getPrice() {
        return fPrice;
    }

    public String getShortInfo() {
        return getLongName();
    }

    public String getBillItemText(Country country) {
        return getLongName();
    }

    private String getLongName() {
        return getAdditionalName() != null ? getName() + " " + getAdditionalName() : getName();
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getContractNo() {
        if (getId() == null)
            return "";
        return serviceId(getId().intValue()).contractNo().toString();
    }

    public Integer getContract() {
        return fContract;
    }

    public void setContract(final Integer fContract) {
        this.fContract = fContract;
    }

    public String getStatus() {
        return fStatus;
    }

    public void setStatus(String fStatus) {
        this.fStatus = fStatus;
    }

    public Integer getAddressId() {
        return fAddressId;
    }

    public void setAddressId(Integer fAddressId) {
        this.fAddressId = fAddressId;
    }

    public Boolean getIncludeDph() {
        return fIncludeDph;
    }

    public void setIncludeDph(Boolean fDph) {
        this.fIncludeDph = fDph;
    }

    public Integer getProductId() {
        return fProductId;
    }

    public void setProductId(Integer fProductId) {
        this.fProductId = fProductId;
    }
}
package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

	private Boolean fIsConnectivity;

	private Connectivity fConnectivity = new Connectivity();

	private String fInfo;

	// ~ Methods
	// ----------------------------------------------------------------

	public void setConnectivity(Connectivity connectivity) {
		fConnectivity = connectivity;
	}

	public Connectivity getConnectivity() {
		return fConnectivity;
	}

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
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(
				getShortInfo()).append(
				(getPeriod() != null) ? getPeriod().getHistoricToString()
						: null).append(getFrequency()).append(getPrice())
				.append(getInfo()).toString();
	}

	public void setInfo(String info) {
		fInfo = info;
	}

	public String getInfo() {
		return fInfo;
	}

	public void setIsConnectivity(Boolean isConnectivity) {
		fIsConnectivity = isConnectivity;
	}

	public Boolean getIsConnectivity() {
		return fIsConnectivity;
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
		StringBuffer shortName = new StringBuffer(getLongName());
		if (getIsConnectivity() && getConnectivity() != null) {
			shortName.append(" ").append(getConnectivity().getDownload())
					.append("/").append(getConnectivity().getUpload());
			if (getConnectivity().getIsAggregated())
				shortName.append(" (&)");
		}
		return shortName.toString();
	}

	public String getBillItemText(Country country) {
		StringBuffer invoiceLine = new StringBuffer(getLongName());
		Connectivity con = getConnectivity();
		if (getIsConnectivity() && con != null) {
			// we have connectivity so pretty print it
			if (con.getIsAggregated() && Country.PL.equals(country)) {
				invoiceLine.append(" do");
			}
			invoiceLine.append(" " + con.getDownload());
			if (con.getUpload() != null) {
				invoiceLine.append("/" + con.getUpload());
			}
			invoiceLine.append(" kbps");
			if (con.getIsAggregated() && Country.CZ.equals(country)) {
				invoiceLine.append(" FUP");
			}
		}
		return invoiceLine.toString();
	}

	private String getLongName() {
		return getAdditionalName() != null ? getName() + " "
				+ getAdditionalName() : getName();
	}

	public String getAdditionalName() {
		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}
}
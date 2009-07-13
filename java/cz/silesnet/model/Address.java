package cz.silesnet.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import cz.silesnet.model.enums.Country;

/**
 * Component class to hold address info for customer.
 * 
 * @author Richard Sikora
 */
public class Address implements HistoricToString, Serializable {

	// ~ Instance fields
	// --------------------------------------------------------

	private static final long serialVersionUID = 2297446722831120403L;

	private String fStreet;

	private String fCity;

	private String fPostalCode;

	private Country fCountry = Country.CZ;

	// ~ Methods
	// ----------------------------------------------------------------

	public void setCity(String city) {
		fCity = city;
	}

	public String getCity() {
		return fCity;
	}

	public String getHistoricToString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(
				getStreet()).append(getCity()).append(getPostalCode()).append(
				getCountry()).toString();
	}

	public void setPostalCode(String postalCode) {
		fPostalCode = postalCode;
	}

	public String getPostalCode() {
		return fPostalCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public Country getCountry() {
		return fCountry;
	}

	public void setCountry(Country country) {
		fCountry = country;
	}

	public String getStreet() {
		return fStreet;
	}

	public void setStreet(String street) {
		fStreet = street;
	}

	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
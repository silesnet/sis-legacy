package cz.silesnet.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * General purpose class to store various contact info.
 * 
 * @author Richard Sikora
 * 
 */
public class Contact implements HistoricToString, Serializable {

	private static final long serialVersionUID = 694162444503592329L;

	private String fName;

	private Address fAddress = new Address();

	private String fEmail;

	private String fPhone;

	public Address getAddress() {
		return fAddress;
	}

	public void setAddress(Address address) {
		fAddress = address;
	}

	public String getEmail() {
		return fEmail != null ? fEmail.replace(" ", "") : null;
	}

	public void setEmail(String email) {
		fEmail = email != null ? email.replace(" ", "") : null;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public String getPhone() {
		return fPhone;
	}

	public void setPhone(String phone) {
		fPhone = phone;
	}

	public String getHistoricToString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(
				getName()).append(
				getAddress() != null ? getAddress().getHistoricToString() : "")
				.append(getEmail()).append(getPhone()).toString();
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public String getPhone1() {
		String phoneStr = getPhone() != null ? getPhone().replace(" ", "") : "";
		String[] phones = phoneStr.split(",");
		return phones.length >= 1 ? phones[0] : "";
	}

	public String getPhone2() {
		// get space less phones
		String phoneStr = getPhone() != null ? getPhone().replace(" ", "") : "";
		String[] phones = phoneStr.split(",");
		return phones.length >= 2 ? phones[1] : "";
	}

	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}

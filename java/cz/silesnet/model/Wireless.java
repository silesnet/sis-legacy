package cz.silesnet.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import cz.silesnet.model.enums.Polarization;
import cz.silesnet.model.enums.WirelessEnum;
import cz.silesnet.model.enums.WirelessFrequency;

/**
 * Wireless node entity.
 * 
 * @author Richard Sikora
 */
public class Wireless extends Node {

	private static final long serialVersionUID = -9020138605893290201L;

	private WirelessEnum type = WirelessEnum.AP;

	private WirelessFrequency frequency;

	private String mac;

	private String macVendor;

	private String customVendor;

	private Label domain;

	private String route;

	private String ip;

	private String wep;

	private String ssid;

	private Boolean macAuthorization;

	private Polarization polarization = Polarization.HORIZONTAL;

	public Label getDomain() {
		return domain;
	}

	public void setDomain(Label domain) {
		this.domain = domain;
	}

	public WirelessFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(WirelessFrequency frequency) {
		this.frequency = frequency;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public String getMacFormatted() {
		// return formatted mac with ':' eg. 12:EF:45:44:12:65
		return StringUtils.isNotBlank(mac) ? StringUtils.chomp(mac.replaceAll(
				"(..)", "$1:"), ":") : mac;
	}

	public void setMac(String mac) {
		// remove all not needed chars from given mac
		this.mac = mac != null ? mac.toUpperCase().replaceAll("[^0-9A-F]", "")
				: null;
	}

	public Boolean isMacAuthorization() {
		return macAuthorization;
	}

	public Boolean getMacAuthorization() {
		return macAuthorization;
	}

	public void setMacAuthorization(Boolean macAuthorization) {
		this.macAuthorization = macAuthorization;
	}

	public Polarization getPolarization() {
		return polarization;
	}

	public void setPolarization(Polarization polarization) {
		this.polarization = polarization;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public WirelessEnum getType() {
		return type;
	}

	public void setType(WirelessEnum type) {
		this.type = type;
	}

	public String getMacVendor() {
		return macVendor;
	}

	public void setMacVendor(String macVendor) {
		this.macVendor = macVendor;
	}

	public String getCustomVendor() {
		return customVendor;
	}

	public void setCustomVendor(String customVendor) {
		this.customVendor = customVendor;
	}

	public String getVendor() {
		// custom vendor takes precedence
		return StringUtils.isNotBlank(customVendor) ? customVendor : macVendor;
	}

	public String getWep() {
		return wep;
	}

	public String getShortWep() {
		if (StringUtils.isNotBlank(wep)) {
			// substring starting with '*' or '-' followed by anythink but ' '
			// or \n
			Pattern wepPattern = Pattern.compile("[\\*-]([^ \\n]*)");
			Matcher wepMatcher = wepPattern.matcher(wep);
			if (wepMatcher.find())
				return wepMatcher.group(1);
		}
		return wep;
	}

	public void setWep(String wep) {
		this.wep = wep;
	}
}
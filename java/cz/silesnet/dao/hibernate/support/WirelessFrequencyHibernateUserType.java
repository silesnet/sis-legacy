package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.WirelessFrequency;

public class WirelessFrequencyHibernateUserType extends
		IntEnumHibernateUserType<WirelessFrequency> {

	public WirelessFrequencyHibernateUserType() {
		super(WirelessFrequency.F2412);
	}

}

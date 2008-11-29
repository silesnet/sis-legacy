package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.BillingStatus;

/**
 * Class for BillingStatus enum Hibernate mapping.
 * 
 * @author Richard Sikora
 */
public class BillingStatusHibernateUserType  extends IntEnumHibernateUserType<BillingStatus>{

	public BillingStatusHibernateUserType() {
		super(BillingStatus.INVOICE);
	}

}

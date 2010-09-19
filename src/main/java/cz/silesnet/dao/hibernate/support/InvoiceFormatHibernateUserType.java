package cz.silesnet.dao.hibernate.support;

import cz.silesnet.service.invoice.InvoiceFormat;

/**
 * Class for InvoiceFormat enum Hibernate mapping.
 *
 * @author Richard Sikora
 */
public class InvoiceFormatHibernateUserType extends
    IntEnumHibernateUserType<InvoiceFormat> {

  // ~ Constructors
  // -----------------------------------------------------------

  public InvoiceFormatHibernateUserType() {
    super(InvoiceFormat.LINK);
  }
}
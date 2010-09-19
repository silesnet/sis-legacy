package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.Country;

/**
 * Class for Country enum Hibernate mapping.
 *
 * @author Richard Sikora
 */
public class CountryHibernateUserType extends IntEnumHibernateUserType<Country> {

  // ~ Constructors
  // -----------------------------------------------------------

  public CountryHibernateUserType() {
    super(Country.CZ);
  }
}
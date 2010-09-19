package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.Polarization;

public class PolarizationHibernateUserType extends
    IntEnumHibernateUserType<Polarization> {

  public PolarizationHibernateUserType() {
    super(Polarization.HORIZONTAL);
  }

}

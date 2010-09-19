package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.WirelessEnum;

public class WirelessEnumHibernateUserType extends
    IntEnumHibernateUserType<WirelessEnum> {

  public WirelessEnumHibernateUserType() {
    super(WirelessEnum.AP);
  }

}

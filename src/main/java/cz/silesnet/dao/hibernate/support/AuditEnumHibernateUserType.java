package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.AuditEnum;

public class AuditEnumHibernateUserType extends
    IntEnumHibernateUserType<AuditEnum> {

  public AuditEnumHibernateUserType() {
    super(AuditEnum.SYSTEM);
  }

}

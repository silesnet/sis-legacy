package cz.silesnet.model.enums;

public enum AuditEnum implements EnumPersistenceMapping<AuditEnum> {
  SYSTEM(10, "enum.audit.billing"), BILLING(20, "enum.audit.system"), ENTITY(
      100, "enum.audit.entity"), ENTITY_NODE(110,
      "enum.audit.entity.node"), ENTITY_CUSTOMER(120,
      "enum.audit.entity.customer");

  private int id;

  private String name;

  // has to declare it by hand
  private static ReverseEnumMap<AuditEnum> reverseMap = new ReverseEnumMap<AuditEnum>(
      AuditEnum.class);

  private AuditEnum(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return getName();
  }

  public int getId() {
    return id;
  }

  public AuditEnum valueOf(int id) {
    return reverseMap.get(id);
  }

}

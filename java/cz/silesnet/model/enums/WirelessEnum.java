package cz.silesnet.model.enums;

public enum WirelessEnum implements EnumPersistenceMapping<WirelessEnum>{
	AP	(10, "enum.wireless.ap"),
	BR	(20, "enum.wireless.br"),
	SA	(30, "enum.wireless.sa");

	private int id;
	private String name;
	// has to declare it by hand
	private static ReverseEnumMap<WirelessEnum> reverseMap = new ReverseEnumMap<WirelessEnum>(WirelessEnum.class);
	
	private WirelessEnum(int id, String name) {
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
	
	public WirelessEnum valueOf(int id) {
		return reverseMap.get(id);
	}
}

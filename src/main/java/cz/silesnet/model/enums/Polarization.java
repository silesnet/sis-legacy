package cz.silesnet.model.enums;

public enum Polarization implements EnumPersistenceMapping<Polarization> {
	HORIZONTAL(10, "enum.polarization.horizontal"), VERTICAL(20,
			"enum.polarization.vertical");

	private int id;

	private String name;

	// has to declare it by hand
	private static ReverseEnumMap<Polarization> reverseMap = new ReverseEnumMap<Polarization>(
			Polarization.class);

	private Polarization(int id, String name) {
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

	public Polarization valueOf(int id) {
		return reverseMap.get(id);
	}

}

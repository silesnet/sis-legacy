package cz.silesnet.model.enums;

/**
 * Frequency enumerator for setting servicess/billing periods.
 * 
 * @author Richard Sikora
 * 
 */
public enum Frequency implements EnumPersistenceMapping<Frequency> {
	ONE_TIME(10, "enum.frequency.one_time", 0), DAILY(20,
			"enum.frequency.daily", 0), WEEKLY(30, "enum.frequency.weekly", 0), MONTHLY(
			40, "enum.frequency.monthly", 1), Q(50, "enum.frequency.q", 3), QQ(
			60, "enum.frequency.qq", 6), ANNUAL(70, "enum.frequency.annual", 12);

	private int fId;

	private String fName;

	private int fMonths;

	// has to declare it by hand
	private static ReverseEnumMap<Frequency> sReverseMap = new ReverseEnumMap<Frequency>(
			Frequency.class);

	private Frequency(int id, String name, int months) {
		fId = id;
		fName = name;
		fMonths = months;
	}

	public String getName() {
		return fName;
	}

	public String toString() {
		return getName();
	}

	public int getId() {
		return fId;
	}

	public Frequency valueOf(int id) {
		return sReverseMap.get(id);
	}

	public int getMonths() {
		return fMonths;
	}
}
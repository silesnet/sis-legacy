package cz.silesnet.model.enums;

import java.util.EnumSet;

/**
 * Billing status of customer.
 * 
 * @author Richard Sikora
 */
public enum BillingStatus implements EnumPersistenceMapping<BillingStatus> {
	INVOICE		(10, "enum.status.invoice"),
	CEASE		(20, "enum.status.cease"),
	DEADHEAD	(25, "enum.stuats.deadhead"),
	CELL		(30, "enum.status.cell"),
	VIP			(40, "enum.status.vip"),
	PROMOTION	(50, "enum.status.promotion"),
	EXPIRED		(60, "enum.status.expired"),
	JURIST		(70, "enum.status.jurist");

	private int fId;
	private String fName;
	// has to declare it by hand
	private static ReverseEnumMap<BillingStatus> sReverseMap = new ReverseEnumMap<BillingStatus>(BillingStatus.class);
	
	private BillingStatus(int id, String name) {
		fId = id;
		fName = name;
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
	
	public BillingStatus valueOf(int id) {
		return sReverseMap.get(id);
	}
	
	public static EnumSet<BillingStatus> getActiveStatuses() {
		return EnumSet.of(BillingStatus.INVOICE,
						  BillingStatus.CEASE,
						  BillingStatus.DEADHEAD,
						  BillingStatus.CELL,
						  BillingStatus.VIP,
						  BillingStatus.PROMOTION);
	}

	public static EnumSet<BillingStatus> getInactiveStatuses() {
		return EnumSet.of(BillingStatus.EXPIRED,
				  BillingStatus.JURIST);
	}
	
}

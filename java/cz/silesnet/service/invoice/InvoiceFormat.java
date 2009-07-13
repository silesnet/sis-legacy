package cz.silesnet.service.invoice;

import cz.silesnet.model.enums.EnumPersistenceMapping;
import cz.silesnet.model.enums.ReverseEnumMap;

/**
 * Enum for different InvoiceWriter formats.
 * 
 * @author Richard Sikora
 * 
 */
public enum InvoiceFormat implements EnumPersistenceMapping<InvoiceFormat> {
	LINK(10, "enum.format.link"), DUO(20, "enum.format.duo"), HTML(30,
			"enum.format.html"), PDF(40, "enum.format.pdf");

	private int id;

	private String name;

	// has to declare it by hand
	private static ReverseEnumMap<InvoiceFormat> sReverseMap = new ReverseEnumMap<InvoiceFormat>(
			InvoiceFormat.class);

	private InvoiceFormat(int id, String name) {
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

	public InvoiceFormat valueOf(int id) {
		return sReverseMap.get(id);
	}

}

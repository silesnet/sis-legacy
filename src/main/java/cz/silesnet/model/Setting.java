package cz.silesnet.model;

/**
 * Entity class used for storing system setting.
 * 
 * @author Richard Sikora
 */

public class Setting extends Entity {

	private static final long serialVersionUID = -7471033959879511347L;

	private String fName;

	private String fValue;

	public Setting() {
		super();
	}

	/**
	 * @param name
	 * @param value
	 */
	public Setting(String name, String value) {
		super();
		fName = name;
		fValue = value;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public String getValue() {
		return fValue;
	}

	public void setValue(String value) {
		fValue = value;
	}
}

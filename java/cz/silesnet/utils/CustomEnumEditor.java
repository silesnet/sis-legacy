package cz.silesnet.utils;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.model.enums.EnumPersistenceMapping;

/**
 * Custom editor used for binding Enum values of enum classes that implement
 * EnumPersistenceMapping interface.
 * 
 * @author Richard Sikora
 */
public class CustomEnumEditor<E extends Enum<E> & EnumPersistenceMapping<E>>
		extends PropertyEditorSupport {
	protected final Log log = LogFactory.getLog(getClass());

	private E fEnumSample = null;

	public CustomEnumEditor(E enumSample) {
		super();
		fEnumSample = enumSample;
	}

	public void setAsText(String enumIdStr) {
		setValue(fEnumSample.valueOf(Integer.valueOf(enumIdStr)));
	}
}

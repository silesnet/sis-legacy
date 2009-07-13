package cz.silesnet.utils;

import cz.silesnet.service.LabelManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditorSupport;

/**
 * Property editor to set labels object property in jsp forms.
 * 
 * @author Richard Sikora
 */
public class CustomLabelEditor extends PropertyEditorSupport {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	LabelManager lmgr;

	// ~ Constructors
	// -----------------------------------------------------------

	private CustomLabelEditor() {
	}

	public CustomLabelEditor(LabelManager labelManager) {
		super();
		lmgr = labelManager;
	}

	// ~ Methods
	// ----------------------------------------------------------------

	public void setAsText(String labelString) {
		setValue(lmgr.getLabelById(Long.valueOf(labelString)));
	}
}
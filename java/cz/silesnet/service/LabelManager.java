package cz.silesnet.service;

import cz.silesnet.model.Label;

import java.util.List;
import java.util.Map;

/**
 * Label manager interface to provide labels for marking objects.
 * 
 * @author Vlastn�k
 */
public interface LabelManager extends PersistenceManager<Label> {

	// ~ Methods
	// ----------------------------------------------------------------

	public Label getLabelById(Long labelId);

	public List getSubLabels(Label label);

	public Map getSubLabelsMap(long parentId);
}
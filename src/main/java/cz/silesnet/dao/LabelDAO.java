package cz.silesnet.dao;

import java.util.List;

import cz.silesnet.model.Label;

/**
 * An interface to manipulate Labels for stored objects.
 * 
 * @author Richard Sikora
 */
public interface LabelDAO extends DAO {

	public static Long LOGIN_HISTORY_TYPE_LABEL_ID = Long.valueOf(17);

	// ~ Methods
	// ----------------------------------------------------------------

	public Label getLabelById(Long labelId);

	public List<Label> getAll();

	public List<Label> getByExmaple(Label example);

	public List getSubLabels(Long labelId);

	public void removeLabel(Label label);

	public void saveLabel(Label label);
}
package cz.silesnet.dao;

import java.util.Iterator;
import java.util.List;

import cz.silesnet.model.Label;

/**
 * An interface to manipulate Labels for stored objects.
 * 
 * @author Richard Sikora
 */
public interface LabelDAO extends DAO {

	public static Long LOGIN_HISTORY_TYPE_LABEL_ID = 17L;
    
	public Label getLabelById(Long labelId);

	public List<Label> findAll();

	public List<Label> getByExmaple(Label example);

	public List<Label> getSubLabels(Long labelId);

	public void removeLabel(Label label);

	public void saveLabel(Label label);
}
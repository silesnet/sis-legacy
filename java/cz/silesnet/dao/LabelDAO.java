package cz.silesnet.dao;

import cz.silesnet.model.Label;

import java.util.List;

/**
 * An interface to manipulate Labels for stored objects.
 *
 * @author Richard Sikora
 */
public interface LabelDAO
    extends DAO {

    //~ Methods ----------------------------------------------------------------

    public Label getLabelById(Long labelId);

    public List<Label> getAll();
    
    public List<Label> getByExmaple(Label example);

    public List getSubLabels(Long labelId);

    public void removeLabel(Label label);

    public void saveLabel(Label label);
}
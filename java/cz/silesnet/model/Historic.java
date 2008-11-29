package cz.silesnet.model;


/**
 * Interface for object with history tracking ability.
 *
 * @author Richard Sikora
 */
public interface Historic {

    //~ Methods ----------------------------------------------------------------

    public String[] getDiffExcludeFields();

    public Long getHistoryId();

    public Long getHistoryTypeLabelId();
}
package cz.silesnet.model;

/**
 * Class for labeling stored objects.
 *
 * @author Richard Sikora
 */
public class Label extends Entity implements HistoricToString {

  public static final long RESPONSIBLES = 300;

  // ~ Static fields/initializers
  // ---------------------------------------------

  private static final long serialVersionUID = -3182140785402097375L;

  // ~ Instance fields
  // --------------------------------------------------------

  private Long fParentId;

  private String fName;

  // ~ Methods
  // ----------------------------------------------------------------

  public String getHistoricToString() {
    return getName();
  }

  public void setName(String name) {
    fName = name;
  }

  public String getName() {
    return fName;
  }

  public void setParentId(Long parentId) {
    fParentId = parentId;
  }

  public Long getParentId() {
    return fParentId;
  }
}
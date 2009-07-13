package cz.silesnet.model;

/**
 * General network node class.
 * 
 * @author Richard Sikora
 */
public class Node extends Entity implements Historic {

	// ~ Static fields/initializers
	// ---------------------------------------------

	private static final long serialVersionUID = -6334210479231267059L;

	// FIXME shold do it more elegant!!!
	// it depends on id value in db !!!
	private static final Long historyTypeLabelId = Long.valueOf(16);

	private static final String[] diffExcludeFields = { "id", "historyId",
			"subNodesCount", "historyTypeLabelId", "class$0",
			"diffExcludeFields", "vendorMac", "serialVersionUID" };

	private Long parentId;

	private String name;

	private Integer subNodesCount;

	private String info;

	private Boolean active = true;

	private Long historyId;

	// Accessors --------------------------------------------------------------

	public Boolean isActive() {
		return active;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String[] getDiffExcludeFields() {
		return diffExcludeFields;
	}

	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getSubNodesCount() {
		return subNodesCount;
	}

	public void setSubNodesCount(Integer subNodesCount) {
		this.subNodesCount = subNodesCount;
	}

	public Long getHistoryTypeLabelId() {
		return historyTypeLabelId;
	}

}
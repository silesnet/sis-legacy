package cz.silesnet.service.impl;

import cz.silesnet.dao.LabelDAO;

import cz.silesnet.model.Label;
import cz.silesnet.service.LabelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * LabelManager implementation for manipulating labels in order 2 mark objects.
 * 
 * @author Richard Sikora
 */
public class LabelManagerImpl implements LabelManager {

	// ~ Instance fields
	// --------------------------------------------------------

	private LabelDAO dao;

	// ~ Methods
	// ----------------------------------------------------------------

	public Label getLabelById(Long labelId) {
		return dao.getLabelById(labelId);
	}

	// used by spring DI
	public void setLabelDAO(LabelDAO labelDAO) {
		this.dao = labelDAO;
	}

	public List getSubLabels(Label label) {
		return dao.getSubLabels(label.getId());
	}

	public HashMap<Long, String> getSubLabelsMap(long parentId) {
		ArrayList<Label> llist = (ArrayList<Label>) dao.getSubLabels(parentId);
		HashMap<Long, String> lmap = new HashMap<Long, String>();

		// fill map with id, name of received labels
		for (Label l : llist)
			lmap.put(l.getId(), l.getName());

		return lmap;
	}

	public Label get(Long id) {
		return dao.getLabelById(id);
	}

	@SuppressWarnings("unchecked")
	public List<Label> getAll() {
		return dao.getAll();
	}

	public List<Label> getByExample(Label example) {
		return dao.getByExmaple(example);
	}

	public void insert(Label entity) {
		dao.saveLabel(entity);
	}

	public void update(Label entity) {
		dao.saveLabel(entity);
	}

	public void delete(Label entity) {
		dao.removeLabel(entity);
	}
}

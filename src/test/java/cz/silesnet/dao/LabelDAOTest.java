package cz.silesnet.dao;

import cz.silesnet.model.Label;

import java.util.ArrayList;

public class LabelDAOTest extends BaseDAOTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testGetters() {
		LabelDAO dao = (LabelDAO) ctx.getBean("labelDAO");

		Label lab = new Label();
		lab.setName("Test Type");
		lab.setParentId(Long.valueOf(0));

		// save root of Test Type labels
		dao.saveLabel(lab);

		// store parent label id
		assertNotSame(lab.getId(), 0);

		long parentid = lab.getId();

		// have some sublabels
		lab.setId(null);
		lab.setName("TT Label 1");
		lab.setParentId(parentid);
		dao.saveLabel(lab);

		lab.setId(null);
		lab.setName("TT Label 2");
		lab.setParentId(parentid);
		dao.saveLabel(lab);

		lab.setId(null);
		lab.setName("TT Label 3");
		lab.setParentId(parentid);
		dao.saveLabel(lab);

		lab.setId(null);
		lab.setName("TT Label 4");
		lab.setParentId(parentid);
		dao.saveLabel(lab);

		lab = null;

		// try to get all sublabels from id we stored

		// first get parent label by id
		lab = dao.getLabelById(parentid);
		assertNotNull(lab);
		log.debug("Retrieved root Test Type label : " + lab);

		// now get sublabels
		ArrayList<Label> sublabels = (ArrayList<Label>) dao.getSubLabels(lab
				.getId());
		assertNotSame(sublabels.size(), 0);
		log.debug("Retrieved sublabels of Test Type : " + sublabels);

		// do some clean up
		for (Label l : sublabels)
			dao.removeLabel(l);

		// remove also root Test Type label
		dao.removeLabel(lab);
	}

	public void testSaveRemoveLabel() {
		LabelDAO dao = (LabelDAO) ctx.getBean("labelDAO");

		Label lab = new Label();
		lab.setName("AP Type");
		lab.setParentId(Long.valueOf(0));

		// save label
		dao.saveLabel(lab);

		// store label id
		long labelid = lab.getId();

		// remove label
		dao.removeLabel(lab);

		// when tryin to get not existing label return null
		assertNull(dao.getLabelById(labelid));
	}
}

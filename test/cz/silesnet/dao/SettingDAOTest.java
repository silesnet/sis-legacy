package cz.silesnet.dao;

import java.util.List;

import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Setting;

public class SettingDAOTest extends BaseDAOTestCase {

	public void testCRUD() {
		// get dao implementation from application context
		SettingDAO dao = (SettingDAO) ctx.getBean("settingDAO");

		// have mixture
		Setting s = PrepareMixture.getSetting();
		String sName = s.getName();
		String sValue = s.getValue();

		log.debug(s);
		// persist
		dao.save(s);
		assertNotNull(s.getId());
		log.debug("Persisted setting :" + s);
		// store id
		Long sId = s.getId();
		// drop original setting
		s = null;
		// retrieve persisted by id
		s = dao.get(sId);
		assertNotNull(s.getId());
		assertTrue(sId.equals(s.getId()));
		assertTrue(sName.equals(s.getName()));
		assertTrue(sValue.equals(s.getValue()));
		log.debug("Retrieved by Id :" + s);
		// drop original setting
		s = null;
		// retrieve persisted by name
		s = dao.getByName(sName);
		assertNotNull(s.getId());
		assertTrue(sId.equals(s.getId()));
		assertTrue(sName.equals(s.getName()));
		assertTrue(sValue.equals(s.getValue()));
		log.debug("Retrieved by Name :" + s);
		// get all
		List<Setting> settings = dao.getAll();
		assertTrue(settings.size() >= 1);

		Setting s2 = dao.getByName("xx");
		assertTrue(s2 == null);

		// remove test setting
		dao.remove(s);

		// try get removed by name
		s2 = dao.getByName(sName);
		assertTrue(s2 == null);

		// try get removed by id
		s2 = dao.get(sId);
		assertTrue(s2 == null);

	}

}

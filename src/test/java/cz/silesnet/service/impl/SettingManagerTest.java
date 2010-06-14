package cz.silesnet.service.impl;

import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Setting;
import cz.silesnet.service.SettingManager;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SettingManagerTest extends BaseServiceTestCase {

    @Test
    public void testCRUD() {
        SettingManager smgr = (SettingManager) ctx.getBean("settingManager");
        assertNotNull(smgr);
        // have mixture
        Setting s = PrepareMixture.getSetting();
        String sName = s.getName();
        String sValue = s.getValue();
        log.debug(s);
        // persist
        smgr.insert(s);
        assertNotNull(s.getId());
        log.debug("Persisted setting :" + s);
        // store id
        Long sId = s.getId();
        // drop original setting
        s = null;
        // retrieve persisted by name
        s = smgr.get(sName);
        assertNotNull(s.getId());
        assertTrue(sId.equals(s.getId()));
        assertTrue(sName.equals(s.getName()));
        assertTrue(sValue.equals(s.getValue()));
        log.debug("Retrieved by Name :" + s);
        // get all
        List<Setting> settings = smgr.getAll();
        assertTrue(settings.size() >= 1);
        Setting s2 = smgr.get("xx");
        assertTrue(s2 == null);
        // remove test setting
        smgr.delete(s);
        // try get removed by name
        s2 = smgr.get(sName);
        assertTrue(s2 == null);
    }
}

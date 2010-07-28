package cz.silesnet.dao;

import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Setting;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Test(groups = "integration")
public abstract class SettingDAOTest extends DaoTestSupport<SettingDAO> {
  @Test
  public void testCRUD() {

    // have mixture
    Setting s = PrepareMixture.getSetting();
    String sName = s.getName();
    String sValue = s.getValue();

    log.debug(s);
    // persist
    dao.save(s);
    assertThat(s.getId(), is(not(nullValue())));

    log.debug("Persisted setting :" + s);
    // store id
    Long sId = s.getId();
    // drop original setting
    s = null;
    // retrieve persisted by id
    s = dao.get(sId);
    assertThat(s.getId(), is(not(nullValue())));
    assertThat(sId.equals(s.getId()), is(true));
    assertThat(s.getName(), is(sName));
    assertThat(s.getValue(), is(sValue));
    log.debug("Retrieved by Id :" + s);
    // drop original setting
    s = null;
    // retrieve persisted by name
    s = dao.getByName(sName);
    assertThat(s.getId(), is(not(nullValue())));
    assertThat(sId.equals(s.getId()), is(true));
    assertThat(s.getName(), is(sName));
    assertThat(s.getValue(), is(sValue));
    log.debug("Retrieved by Name :" + s);
    // get all
    List<Setting> settings = dao.getAll();
    assertThat(settings.size() >= 1, is(true));

    Setting s2 = dao.getByName("xx");
    assertThat(s2, is(nullValue()));
    // remove test setting
    dao.remove(s);

    // try get removed by name
    s2 = dao.getByName(sName);
    assertThat(s2, is(nullValue()));

    // try get removed by id
    s2 = dao.get(sId);
    assertThat(s2, is(nullValue()));
  }

}

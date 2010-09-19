package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.SettingDAO;
import cz.silesnet.model.Setting;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of SettingDAO using Hibernate.
 *
 * @author Richard Sikora
 */
public class SettingDAOHibernate extends HibernateDaoSupport implements
    SettingDAO {

  public Setting get(Long settingId) {
    return (Setting) getHibernateTemplate().get(Setting.class, settingId);
  }

  public Setting getByName(String name) {
    List<Setting> settings = (ArrayList<Setting>) getHibernateTemplate()
        .find("from Setting s where s.name=?", name);
    if (settings == null || settings.size() == 0)
      return null;
    if (settings.size() > 1)
      throw new ObjectRetrievalFailureException(Setting.class,
          "More than one setting named :" + name + "found!");
    return settings.get(0);
  }

  public List<Setting> getAll() {
    return (ArrayList<Setting>) getHibernateTemplate().find("from Setting");
  }

  public void save(Setting setting) {
    getHibernateTemplate().saveOrUpdate(setting);
  }

  public void remove(Setting setting) {
    getHibernateTemplate().delete(setting);
  }

}

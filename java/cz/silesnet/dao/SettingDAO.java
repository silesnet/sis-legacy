package cz.silesnet.dao;

import java.util.List;

import cz.silesnet.model.Setting;

/**
 * An interface to manipulate Labels for stored objects.
 * 
 * @author Richard Sikora
 */

public interface SettingDAO extends DAO {

	public Setting get(Long settingId);

	public Setting getByName(String name);

	public List<Setting> getAll();

	public void save(Setting setting);

	public void remove(Setting setting);
}

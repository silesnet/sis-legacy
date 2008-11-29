package cz.silesnet.service.impl;

import java.util.List;

import cz.silesnet.dao.SettingDAO;
import cz.silesnet.model.Setting;
import cz.silesnet.service.SettingManager;

/**
 * Concrete implementation of setting manager.
 * 
 * @author Richard Sikora
 */
public class SettingManagerImpl implements SettingManager {
	
	private SettingDAO dao;
	
	public void setSettingDAO(SettingDAO settingDAO) {
		dao = settingDAO;
	}

	public Setting get(String name) {
		return dao.getByName(name);
	}

	public void insert(Setting setting) {
		dao.save(setting);
	}

	public void update(Setting setting) {
		dao.save(setting);
	}

	public void delete(Setting setting) {
		dao.remove(setting);
	}

	public List<Setting> getAll() {
		return dao.getAll();
	}

	public Long getLong(String name) {
		Long aValue = null;
		try {
			aValue = Long.valueOf(get(name).getValue());
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {}
		return aValue;
	}

	public Long getLong(String name, Long fallBack) {
		Long aValue = getLong(name);
		return aValue != null ? aValue : fallBack;
	}

	public Double getDouble(String name) {
		Double aValue = null;
		try {
			aValue = Double.valueOf(get(name).getValue());
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {}
		return aValue;
	}

	public Double getDouble(String name, Double fallBack) {
		Double aValue = getDouble(name);
		return aValue != null ? aValue : fallBack;
	}

	public Integer getInteger(String name) {
		Integer aValue = null;
		try {
			aValue = Integer.valueOf(get(name).getValue());
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {}
		return aValue;
	}

	public Integer getInteger(String name, Integer fallBack) {
		Integer  aValue = getInteger(name);
		return aValue != null ? aValue : fallBack;
	}

	public String getString(String name, String fallBack) {
		Setting setting = get(name);
		return setting != null ? setting.getValue() : fallBack;
	}
}

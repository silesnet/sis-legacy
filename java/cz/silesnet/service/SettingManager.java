package cz.silesnet.service;

import java.util.List;

import cz.silesnet.model.Setting;

/**
 * Inrerface defining behavior of system settings.
 * 
 * @author Richard Sikora
 */
public interface SettingManager {

	public Setting get(String name);

	public Long getLong(String name);

	public Long getLong(String name, Long fallBack);

	public Double getDouble(String name);

	public Double getDouble(String name, Double fallBack);

	public void insert(Setting setting);

	public void update(Setting setting);

	public void delete(Setting setting);

	public List<Setting> getAll();

	public Integer getInteger(String name);

	public Integer getInteger(String name, Integer fallBack);

	public String getString(String name, String fallBack);

}

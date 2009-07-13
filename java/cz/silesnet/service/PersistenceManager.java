package cz.silesnet.service;

import java.util.List;

import cz.silesnet.model.Entity;

public interface PersistenceManager<E extends Entity> {
	public E get(Long id);

	public List<E> getAll();

	public List<E> getByExample(E example);

	public void insert(E entity);

	public void update(E entity);

	public void delete(E entity);
}

package cz.silesnet.dao.hibernate.support;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import cz.silesnet.model.enums.EnumPersistenceMapping;

/**
 * UserType for enum <-> int hibernate mapping fields.
 * 
 * @author Richard Sikora
 */
public class IntEnumHibernateUserType<E extends Enum<E> & EnumPersistenceMapping<E>>
		implements UserType {
	private E fEnumSample = null;

	public IntEnumHibernateUserType(E enumSample) {
		fEnumSample = enumSample;
	}

	private static final int[] SQL_TYPES = { Types.INTEGER };

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Class returnedClass() {
		return fEnumSample.getClass();
	}

	public boolean equals(Object obj1, Object obj2) throws HibernateException {
		if (obj1 == obj2)
			return true;
		if (obj1 == null || obj2 == null)
			return false;
		return obj1.equals(obj2);
	}

	public int hashCode(Object obj) throws HibernateException {
		return obj.hashCode();
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {
		return fEnumSample.valueOf(resultSet.getInt(names[0]));
	}

	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement preparedStatement, Object value,
			int index) throws HibernateException, SQLException {
		E enumValue = null;
		try {
			enumValue = (E) value;
		}
		catch (ClassCastException e) {
		}

		if (enumValue == null)
			preparedStatement.setNull(index, Types.INTEGER);
		else
			preparedStatement.setInt(index, enumValue.getId());
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}
}

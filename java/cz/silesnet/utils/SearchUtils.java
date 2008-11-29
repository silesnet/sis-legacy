package cz.silesnet.utils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import cz.silesnet.model.Entity;
import cz.silesnet.model.enums.EnumPersistenceMapping;

/**
 * Methods for database i18n strings seraching.
 * 
 * @author Richard Sikora
 */

public class SearchUtils {

//	private static String sFromChars =	"e��������ͣ����؊�?���ݎ?����������������?����.-,;:&+ ";
//	private static String sFromChars =	"�?�???�??��???��?�??�?�ݎ??�?�???�??��???��?�??�?��??.-,;:&+ ";
	private static String sFromChars =	"ÁĄÄČĆĎÉĚĘËÍŁŇŃÓÖŘŠŚŤÚŮÜÝŽŻŹáąäčćďéěęëíłňńóöřšśťúůüýžżź.-,;:&+ ";
	private static String sToChars = 	"aaaccdeeeeilnnoorsstuuuyzzzaaaccdeeeeilnnoorsstuuuyzzz";
	
	public static String getFromChars() {
		return sFromChars;
	}

	public static String getToChars() {
		return sToChars;
	}
	
	public static String getTranslateColumn(String arg) {
		return "lower(translate(" + arg + ",'" + getFromChars() + "','" + getToChars() + "'))";
	}

	public static String translate(String s) {
		return StringUtils.replaceChars(s, getFromChars(), getToChars()).toLowerCase();
	}
	
	public static String getTranslateOrder(String col) {
		return "translate(" + col + ",'" + getFromChars() + "','" + getToChars() + "')";
	}
	
    public static void addIlikeRestriction(DetachedCriteria crit, String col, String value) {
    	if (value != null)
    		crit.add(Restrictions.sqlRestriction(col + " ilike('" + value + "%')"));
    }

    public static void addIlikeInRestriction(DetachedCriteria crit, String col, String value) {
    	if (value != null)
    		crit.add(Restrictions.sqlRestriction(col + " ilike('%" + value + "%')"));
    }

    public static void addIlikeRestrictionI18n(DetachedCriteria crit, String col, String value) {
    	if (value != null)
    		addIlikeRestriction(crit, getTranslateColumn(col), translate(value));
    }
    
    public static void addIlikeInRestrictionI18n(DetachedCriteria crit, String col, String value) {
    	if (value != null)
    		addIlikeInRestriction(crit, getTranslateColumn(col), translate(value));
    }

    public static void addEqRestriction(DetachedCriteria crit, String col, Object value) {
		if (value != null) {
			crit.add(Restrictions.sqlRestriction(col + "=" + value));
		}
	}

    public static void addEqRestriction(DetachedCriteria crit, String col, EnumPersistenceMapping<?> enumerator) {
		if (enumerator != null) {
			crit.add(Restrictions.sqlRestriction(col + "=" + enumerator.getId()));
		}
	}
    
    public static void addEqRestriction(DetachedCriteria crit, String col, Entity entity) {
		if (entity != null) {
			crit.add(Restrictions.sqlRestriction(col + "=" + entity.getId()));
		}
	}

}

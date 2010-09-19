package cz.silesnet.utils;

import cz.silesnet.model.Entity;
import cz.silesnet.model.HistoricToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for computing object diffs.
 *
 * @author Richard Sikora
 */
public class DiffUtils {

  // ~ Static fields/initializers
  // ---------------------------------------------

  protected static final Log log = LogFactory.getLog(DiffUtils.class);

  // ~ Methods
  // ----------------------------------------------------------------

  public static Map<String, String[]> getDiffMap(Object formerObject,
                                                 Object currentObject, String[] excludeFields) {
    // if second argument is null get out
    if (currentObject == null)
      throw new IllegalArgumentException(
          "Current object argument is null!");

    // if both argument are not null do check if they are of the same class
    if ((formerObject != null)
        && (!formerObject.getClass().equals(currentObject.getClass())))
      throw new IllegalArgumentException(
          "Trying to compute diff on objects of different classes.");

    log.debug("Computing diff.");

    // get available fields of this class
    List<Field> fields = getAllFields(currentObject.getClass(), null,
        asListArrayNullSafe(excludeFields));

    // resulting diff map
    HashMap<String, String[]> diffMap = new HashMap<String, String[]>();

    // iterate throug all fields and audit changes
    for (Field field : fields) {
      if (log.isDebugEnabled())
        log.debug("Processing field: " + field.getName() + " ("
            + field.getType().getSimpleName() + ")");

      // get field audit name
      String fieldAuditName = getFieldAuditName(currentObject, field);

      // get field values
      Object formerFieldValue = getFieldValue(formerObject, field);
      Object currentFieldValue = getFieldValue(currentObject, field);

      // audit changes
      if (Collection.class.isAssignableFrom(field.getType()))
        // audit collection
        auditCollection((Collection) formerFieldValue,
            (Collection) currentFieldValue, fieldAuditName, diffMap);
      else
        // audit simple value
        audit(formerFieldValue, currentFieldValue, fieldAuditName,
            diffMap);
    }
    return diffMap;
  }

  /**
   * Retrieve fields from given class hierachy (not components) excluding
   * those specified in last parameter.
   *
   * @param objectClass
   * @param childFields
   * @param excludeFieldsNames can not be null
   * @return non componet fields from class hierarchy, ommiting those to
   *         exclude.
   */
  private static List<Field> getAllFields(Class objectClass,
                                          List<Field> childFields, List<String> excludeFieldsNames) {
    List<Field> totalFields = new ArrayList<Field>();

    // if objectClass is of type Object.class return emtpy fields
    if (Object.class.equals(objectClass))
      return totalFields;

    // get declared fields form given class as list
    List<Field> declaredFields = asListArrayNullSafe(objectClass
        .getDeclaredFields());

    // catenate declared + child fields lists
    // filter declared fields by exclude fields and add em
    for (Field field : declaredFields)
      if (!excludeFieldsNames.contains(field.getName()))
        totalFields.add(field);
    if (childFields != null)
      totalFields.addAll(childFields);

    // if parent class is not Object.class get also fields from parent by
    // recurency
    Class parentClass = objectClass.getSuperclass();
    if (!Object.class.equals(parentClass))
      return getAllFields(parentClass, totalFields, excludeFieldsNames);

    // if parent class is Object.class give what we have
    return totalFields;
  }

  private static String getFieldAuditName(Object obj, Field field) {
    String objClass = obj.getClass().getSimpleName();
    if ("Wireless".equals(objClass))
      objClass = "wireless";
    return objClass + "." + field.getName();
  }

  /**
   * Retrieve given field value from object.
   *
   * @param obj
   * @param field
   * @return value of given field or null otherwise.
   */
  private static Object getFieldValue(Object obj, Field field) {
    if (obj == null)
      return null;
    // enable access to field value
    field.setAccessible(true);
    Object value = null;
    try {
      value = field.get(obj);
    }
    catch (Exception e) {
    }
    return value;
  }

  /**
   * Get string representation of object.
   *
   * @param former
   * @return empty string if object is null or somehow empty. Otherwise give
   *         string representation of object (regarding HistoricToString interface).
   */
  private static String getToString(Object obj) {
    // null returns empty string
    if (obj == null)
      return "";

    // if implements HistoricToString return it otherwise standard toString
    if (HistoricToString.class.isAssignableFrom(obj.getClass()))
      return ((HistoricToString) obj).getHistoricToString();
    else {
      if (obj instanceof Date)
        return (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SS"))
            .format(obj);
      else
        return obj.toString();
    }
  }

  /**
   * Returns fixed size list backed by given array.
   *
   * @param array
   * @return a list view of the specified array.
   */
  private static <T extends Object> List<T> asListArrayNullSafe(T[] array) {
    if (array == null)
      return new ArrayList<T>();
    return Arrays.asList(array);
  }

  /**
   * Audit simple (shallow) change on two objects. Values are inspected
   * whthout iterating members.
   *
   * @param former
   * @param current
   * @param fieldAuditName
   * @param diffMap
   */
  private static void audit(Object former, Object current,
                            String fieldAuditName, Map<String, String[]> diffMap) {
    String formerString = getToString(former);
    String currentString = getToString(current);

    if (!formerString.equals(currentString)) {
      // diff found add it to map
      log.info("Auditing change (" + fieldAuditName + ") ["
          + formerString + ", " + currentString + "]");
      diffMap.put(fieldAuditName, new String[]{formerString,
          currentString});
    }
  }

  /**
   * Audit change of each pair of collections values. Values are inspected in
   * simple way without iterating members.
   *
   * @param former         - collection of values
   * @param current        - collection of values
   * @param fieldAuditName - name of collection field
   * @param diffMap        - map for recording changes if any
   */
  private static void auditCollection(Collection former, Collection current,
                                      String fieldAuditName, HashMap<String, String[]> diffMap) {
    log.debug("Processing collection.");
    // get paired elements list
    List<Object[]> paired = getPairedCollections(former, current);
    for (Object[] pair : paired)
      audit(pair[0], pair[1], fieldAuditName, diffMap);
  }

  @SuppressWarnings("unchecked")
  private static List<Object[]> getPairedCollections(Collection former,
                                                     Collection current) {
    ArrayList<Object[]> paired = new ArrayList<Object[]>();
    // null safe collections
    if (former == null)
      former = new ArrayList<Object>();
    if (current == null)
      current = new ArrayList<Object>();
    // have modifiable current collection not touching original one
    Collection<Object> currentTmp = new ArrayList<Object>(current);
    // match all former object
    for (Object formerObj : former) {
      Iterator<Object> currentIt = currentTmp.iterator();
      boolean matched = false;
      while (currentIt.hasNext()) {
        Object currentObj = currentIt.next();
        if (match(formerObj, currentObj)) {
          // found match add matched pair to result
          paired.add(new Object[]{formerObj, currentObj});
          // remove from temporary current collection
          currentIt.remove();
          // set flag
          matched = true;
          // process next formerObj
          break; // while()
        }
      }
      if (!matched) {
        // match not found add unbalanced pair to result
        paired.add(new Object[]{formerObj, null});
      }
    }
    // add all not paired current objects
    for (Object currentObj : currentTmp)
      paired.add(new Object[]{null, currentObj});
    return paired;
  }

  private static boolean match(Object former, Object current) {
    if (former instanceof Entity && current instanceof Entity) {
      if (((Entity) former).getId() != null) {
        if (((Entity) former).getId()
            .equals(((Entity) current).getId()))
          return true;
        else
          return false;

      }
    }
    if (former.equals(current))
      return true;
    else
      return false;
  }
}

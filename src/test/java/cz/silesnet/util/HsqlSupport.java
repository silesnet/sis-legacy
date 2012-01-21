package cz.silesnet.util;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 20, 2010
 * Time: 7:20:47 PM
 */
public class HsqlSupport {
  public static String translate(String value, String from, String to) {
    return org.apache.commons.lang.StringUtils.replaceChars(value, from, to).toLowerCase();
  }


  // NOT ACTUALLY USED
  public static boolean ilike(final String value, final String pattern) {
    Pattern ilike = Pattern.compile(pattern.replaceAll("%", ".*"), Pattern.CASE_INSENSITIVE);
    return ilike.matcher(value).matches();
  }
}

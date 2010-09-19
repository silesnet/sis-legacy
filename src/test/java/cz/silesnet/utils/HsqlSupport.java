package cz.silesnet.utils;

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

}

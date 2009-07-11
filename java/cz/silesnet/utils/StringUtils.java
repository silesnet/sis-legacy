package cz.silesnet.utils;

/**
 * Various utility methods for strings. Implemented to be library independet for
 * core POJO classes.
 * 
 * @author Richard Sikora
 */
public class StringUtils {
	public static void appendNullSafe(StringBuffer sb, String before,
			String main, String after) {
		if (sb == null)
			throw new NullPointerException();
		if (main != null) {
			if (before != null)
				sb.append(before);
			sb.append(main);
			if (after != null)
				sb.append(after);
		}
	}

	public static String trimRight(StringBuffer sb, String chop) {
		// TODO implement it if needed
		return null;
	}
}

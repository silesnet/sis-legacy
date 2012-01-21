package cz.silesnet.util;

/**
 * Various utility methods for strings. Implemented to be library independet for
 * core POJO classes.
 *
 * @author Richard Sikora
 */
public class StringUtils {

    public static String join(final Iterable iterable, final String separator) {
        if (iterable == null)
            throw new IllegalArgumentException("iterable cannot be null");
        if (separator == null)
            throw new IllegalArgumentException("separator cannot be null");
        final StringBuilder builder = new StringBuilder();
        for (Object member : iterable)
            builder.append(member.toString()).append(separator);
        return builder.length() > 0 ? builder.substring(0, builder.length() - separator.length()) : "";
    }


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

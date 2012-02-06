package cz.silesnet.event;

import java.util.regex.Pattern;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:53
 */
public abstract class KeyPattern {
    public abstract boolean matches(Key key);

    public static KeyPattern of(final String regex) {
        return new DefaultKeyPattern(regex);
    }

    public final static class DefaultKeyPattern extends KeyPattern {
        private final Pattern pattern;

        private DefaultKeyPattern(final String regex) {
            this.pattern = Pattern.compile(regex.replaceAll("\\.", "\\.").replaceAll("\\*", ".*"));
        }

        public boolean matches(final Key key) {
            return pattern.matcher(key.toString()).matches();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final DefaultKeyPattern that = (DefaultKeyPattern) o;

            if (pattern != null ? !pattern.equals(that.pattern) : that.pattern != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return pattern != null ? pattern.hashCode() : 0;
        }
    }

}

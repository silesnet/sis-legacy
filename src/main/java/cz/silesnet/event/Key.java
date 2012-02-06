package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:42
 */
public class Key extends KeyPattern {
    private final String key;
    private final int nameIndex;

    public static Key of(final String key) {
        return new Key(key);
    }

    private Key(final String key) {
        if (key == null || key.length() == 0)
            throw new IllegalArgumentException("event of cannot be empty or null");
        if (key.matches(".*\\*.*"))
            throw new IllegalArgumentException("event of cannot contain wildcard characters '" + key + "'");
        final String trimmed = key.replaceAll("\\s", "");
        if (!key.equals(trimmed))
            throw new IllegalArgumentException("event of cannot contain white space characters '" + key + "'");
        if (key.endsWith("."))
            throw new IllegalArgumentException("event of has to contain event name '" + key + "'");
        if (key.matches(".*\\.\\..*"))
            throw new IllegalArgumentException("event of cannot contain empty domains '" + key + "'");
        this.nameIndex = key.lastIndexOf('.') + 1;
        this.key = key;
    }

    public String domain() {
        return nameIndex > 0 ? key.substring(0, nameIndex - 1) : "";
    }
    
    public String name() {
        return key.substring(nameIndex);
    }
    
    public boolean matches(final Key that) {
        return this.key.equals(that.key);
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Key eventKey = (Key) o;

        if (key != null ? !key.equals(eventKey.key) : eventKey.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}

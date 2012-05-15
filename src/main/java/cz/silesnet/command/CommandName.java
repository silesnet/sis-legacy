package cz.silesnet.command;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public final class CommandName {
    private final String name;

    public static CommandName of(final String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("command name cannot be empty or null");
        return new CommandName(name);
    }

    private CommandName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandName that = (CommandName) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

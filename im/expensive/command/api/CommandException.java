/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

public final class CommandException
extends RuntimeException {
    private final String message;

    public CommandException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CommandException)) {
            return false;
        }
        CommandException other = (CommandException)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CommandException;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CommandException(message=" + this.getMessage() + ")";
    }
}


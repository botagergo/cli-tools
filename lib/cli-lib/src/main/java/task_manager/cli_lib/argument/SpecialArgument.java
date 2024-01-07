package task_manager.cli_lib.argument;

import java.util.Set;

public record SpecialArgument(char type, String value) {

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpecialArgument other)) {
            return false;
        }

        return type == other.type && value.equals(other.value);
    }

    public static SpecialArgument from(String arg) throws NotASpecialArgumentException {
        if (!isSpecialArgument(arg)) {
            throw new NotASpecialArgumentException(arg);
        }

        return new SpecialArgument(arg.charAt(0), arg.substring(1));
    }

    public static boolean isSpecialArgument(String value) {
        return (!value.isEmpty() && isSpecialArgumentChar(value.charAt(0)));
    }

    public static boolean isSpecialArgumentChar(char ch) {
        return specialChars.contains(ch);
    }

    public static final Set<Character> specialChars =
            Set.of('!', '@', '/', '?', '#', '+', '*', ':', '<', '>', '&', '=', '%', '.');
}

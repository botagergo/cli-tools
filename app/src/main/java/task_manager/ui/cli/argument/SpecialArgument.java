package task_manager.ui.cli.argument;

import java.util.Set;

public class SpecialArgument {

    public SpecialArgument(char type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpecialArgument)) {
            return false;
        }

        SpecialArgument other = (SpecialArgument) obj;
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

    public char type;
    public String value;

    public static final Set<Character> specialChars =
            Set.of('!', '@', '/', '?', '#', '+', '*', ':', '<', '>', '&', '=', '%');
}

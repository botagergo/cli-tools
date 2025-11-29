package cli_tools.common.cli.argument;

import cli_tools.common.core.data.property.Affinity;

import java.util.Set;

public record SpecialArgument(char type, String value) {

    public static final Set<Character> specialChars =
            Set.of('@', '?', '#', '*', '<', '>', '&', '=', '%', '$');

    public static boolean isSpecialArgumentChar(char ch) {
        return specialChars.contains(ch);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpecialArgument(var otherType, var otherValue))) {
            return false;
        }

        return type == otherType && value.equals(otherValue);
    }
}

package cli_tools.common.cli.argument;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import cli_tools.common.core.data.property.Affinity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Setter
@Getter
public class ArgumentList {

    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';

    private String commandName = null;
    @NonNull private List<String> leadingNormalArguments = new ArrayList<>();
    @NonNull private List<String> trailingNormalArguments = new ArrayList<>();
    @NonNull private LinkedHashMap<Character, List<SpecialArgument>> specialArguments = new LinkedHashMap<>();
    @NonNull private List<PropertyArgument> filterPropertyArguments = new ArrayList<>();
    @NonNull private List<PropertyArgument> modifyPropertyArguments = new ArrayList<>();
    @NonNull private List<OptionArgument> optionArguments = new ArrayList<>();

    public static class ArgumentListException extends Exception {
        public ArgumentListException(String msg) {
            super(msg);
        }
    }

    public ArgumentList(
            String commandName,
            @NonNull List<String> leadingNormalArguments,
            @NonNull List<String> trailingNormalArguments,
            @NonNull LinkedHashMap<Character, List<SpecialArgument>> specialArguments,
            @NonNull List<PropertyArgument> filterPropertyArguments,
            @NonNull List<PropertyArgument> modifyPropertyArguments,
            @NonNull List<OptionArgument> optionArguments
    ) {
        this.commandName = commandName;
        this.leadingNormalArguments = leadingNormalArguments;
        this.trailingNormalArguments = trailingNormalArguments;
        this.specialArguments = specialArguments;
        this.filterPropertyArguments = filterPropertyArguments;
        this.modifyPropertyArguments = modifyPropertyArguments;
        this.optionArguments = optionArguments;
    }

    public ArgumentList() {}

    public static ArgumentList from(List<String> tokens) throws ArgumentListException {
        if (tokens.isEmpty()) {
            return new ArgumentList();
        }
        ArgumentList argList = new ArgumentList();
        for (String token : tokens) {
            parseAndAddArgument(token, argList);
        }
        return argList;
    }

    private static void parseAndAddArgument(String token, ArgumentList argList) throws ArgumentListException {
        StringBuilder currentToken = new StringBuilder();
        boolean isPropertyArg = false;
        char currentQuote = 0;
        boolean quoteFound = false;
        for (int index = 0; index < token.length(); index++) {
            char currentChar = token.charAt(index);
            if (currentChar == '\\') {
                index++;
                if (index < token.length()) {
                    currentToken.append(token.charAt(index));
                }
                continue;
            }
            if (currentChar == SINGLE_QUOTE || currentChar == DOUBLE_QUOTE) {
                if (currentQuote == 0) {
                    currentQuote = currentChar;
                    quoteFound = true;
                } else if (currentChar == currentQuote) {
                    currentQuote = 0;
                }
            } else if (currentQuote != 0) {
                currentToken.append(currentChar);
                continue;
            }
            if ("+-.".indexOf(currentChar) >= 0) {
                isPropertyArg = true;
            } else if (currentChar == ':') {
                if (quoteFound) {
                    throw new ArgumentListException("quotes are not allowed on the left side of property/predicate arguments");
                }
                parsePropertyArgument(currentToken.toString(), token.substring(index + 1), argList);
                return;
            }
            currentToken.append(currentChar);
            if (isPropertyArg && index == token.length() - 1) {
                parsePropertyArgument(currentToken.toString(), null, argList);
                return;
            }
        }
        if (argList.commandName == null && token.matches("^[a-z]+$")) {
            argList.commandName = token;
        } else {
            (argList.commandName == null ? argList.leadingNormalArguments : argList.trailingNormalArguments).add(token);
        }
    }

    private static void parsePropertyArgument(String leftPart, String rightPart, ArgumentList argList) {
        boolean skipFirst = false;
        Affinity affinity = Affinity.NEUTRAL;
        boolean isOption = false;
        if (!leftPart.isEmpty()) {
            char firstChar = leftPart.charAt(0);
            if (firstChar == '-') {
                affinity = Affinity.NEGATIVE;
                skipFirst = true;
            } else if (firstChar == '+') {
                affinity = Affinity.POSITIVE;
                skipFirst = true;
            } else if (firstChar == '.') {
                isOption = true;
                skipFirst = true;
            }
            if (skipFirst) {
                leftPart = leftPart.substring(1);
            }
        }
        List<String> valueList = rightPart != null ? getValues(rightPart, ',') : null;
        if (isOption) {
            argList.optionArguments.add(new OptionArgument(leftPart, valueList));
        } else {
            List<String> nameList = getValues(leftPart, '.');
            String propertyName = !nameList.isEmpty() ? nameList.get(0) : "";
            String predicate = nameList.size() >= 2 ? nameList.get(1) : null;
            if (argList.commandName == null) {
                argList.filterPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
            } else {
                argList.modifyPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
            }
        }
    }

    private static ArrayList<String> getValues(String valueListStr, char separator) {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        char currentQuote = 0;
        for (int i = 0; i < valueListStr.length(); i++) {
            char currentChar = valueListStr.charAt(i);
            if (currentChar == '\\') {
                i++;
                if (i < valueListStr.length()) {
                    currentValue.append(valueListStr.charAt(i));
                }
                continue;
            }
            if (currentChar == SINGLE_QUOTE || currentChar == DOUBLE_QUOTE) {
                if (currentQuote == 0) {
                    currentQuote = currentChar;
                } else if (currentChar == currentQuote) {
                    currentQuote = 0;
                }
            } else if (currentQuote != 0) {
                currentValue.append(currentChar);
            } else if (currentChar == separator) {
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(currentChar);
            }
        }
        values.add(currentValue.toString());
        return values;
    }
}
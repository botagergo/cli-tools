package cli_tools.common.cli.argument;

import cli_tools.common.core.data.property.Affinity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ArgumentList {

    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';

    private String commandName = null;
    @NonNull
    private List<String> leadingPositionalArguments = new ArrayList<>();
    @NonNull
    private List<String> trailingPositionalArguments = new ArrayList<>();
    @NonNull
    private List<SpecialArgument> specialArguments = new ArrayList<>();
    @NonNull
    private List<PropertyArgument> filterPropertyArguments = new ArrayList<>();
    @NonNull
    private List<PropertyArgument> modifyPropertyArguments = new ArrayList<>();
    @NonNull
    private List<OptionArgument> optionArguments = new ArrayList<>();

    public ArgumentList(
            String commandName,
            @NonNull List<String> leadingPositionalArguments,
            @NonNull List<String> trailingPositionalArguments,
            @NonNull List<SpecialArgument> specialArguments,
            @NonNull List<PropertyArgument> filterPropertyArguments,
            @NonNull List<PropertyArgument> modifyPropertyArguments,
            @NonNull List<OptionArgument> optionArguments
    ) {
        this.commandName = commandName;
        this.leadingPositionalArguments = leadingPositionalArguments;
        this.trailingPositionalArguments = trailingPositionalArguments;
        this.specialArguments = specialArguments;
        this.filterPropertyArguments = filterPropertyArguments;
        this.modifyPropertyArguments = modifyPropertyArguments;
        this.optionArguments = optionArguments;
    }

    public ArgumentList() {
    }

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
        boolean isOptionArg = false;
        boolean isSpecialArg = false;
        char currentQuote = 0;
        boolean quoteFound = false;

        int index = 0;
        char currentChar;

        if (!token.isEmpty()) {
            currentChar = token.charAt(index);
            if ("+-!.".indexOf(currentChar) >= 0) {
                isPropertyArg = true;
                currentToken.append(currentChar);
                index += 1;
            } else if (currentChar == '/') {
                isOptionArg = true;
                currentToken.append(currentChar);
                index += 1;
            } else if (SpecialArgument.isSpecialArgumentChar(currentChar)) {
                isSpecialArg = true;
                currentToken.append(currentChar);
                index += 1;
            }
        }

        for (; index < token.length(); index++) {
            currentChar = token.charAt(index);
            if (currentChar == '\\') {
                index++;
                if (index < token.length()) {
                    currentToken.append(token.charAt(index));
                }
            } else if (currentChar == SINGLE_QUOTE || currentChar == DOUBLE_QUOTE) {
                if (currentQuote == 0) {
                    currentQuote = currentChar;
                    quoteFound = true;
                } else if (currentChar == currentQuote) {
                    currentQuote = 0;
                }
            } else if (currentQuote != 0) {
                currentToken.append(currentChar);
            } else if (currentChar == ':') {
                if (quoteFound) {
                    throw new ArgumentListException("quotes are not allowed on the left side of property/predicate arguments");
                }
                String rightPart = token.substring(index + 1);
                if (isOptionArg) {
                    parseOptionArgument(currentToken.toString(), rightPart, argList);
                } else {
                    parsePropertyArgument(currentToken.toString(), rightPart, argList);
                }
                return;
            } else {
                if (currentChar =='.') {
                    isPropertyArg = true;
                }
                currentToken.append(currentChar);
            }
        }

        if (isPropertyArg) {
            parsePropertyArgument(currentToken.toString(), null, argList);
        } else if (isOptionArg) {
            parseOptionArgument(currentToken.toString(), null, argList);
        } else if (isSpecialArg) {
            parseSpecialArgument(currentToken.toString(), argList);
        } else if (argList.commandName == null && token.matches("^[a-zA-Z]+$")) {
            argList.commandName = token;
        } else {
            (argList.commandName == null ? argList.leadingPositionalArguments : argList.trailingPositionalArguments).add(currentToken.toString());
        }
    }

    private static void parsePropertyArgument(String leftPart, String rightPart, ArgumentList argList) {
        Affinity affinity = Affinity.NEUTRAL;
        if (!leftPart.isEmpty()) {
            char firstChar = leftPart.charAt(0);
            if (firstChar == '-' || firstChar == '!') {
                affinity = Affinity.NEGATIVE;
                leftPart = leftPart.substring(1);
            } else if (firstChar == '+' || firstChar == '.') {
                affinity = Affinity.POSITIVE;
                leftPart = leftPart.substring(1);
            }
        }
        List<String> valueList = rightPart != null ? getValues(rightPart, ',') : null;
        List<String> nameList = getValues(leftPart, '.');
        String propertyName = !nameList.isEmpty() ? nameList.get(0) : "";
        String predicate = nameList.size() >= 2 ? nameList.get(1) : null;
        if (argList.commandName == null) {
            argList.filterPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
        } else {
            argList.modifyPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
        }
    }

    private static void parseOptionArgument(String leftPart, String rightPart, ArgumentList argList) {
        leftPart = leftPart.substring(1);
        List<String> valueList = rightPart != null ? getValues(rightPart, ',') : null;
        argList.optionArguments.add(new OptionArgument(leftPart, valueList));
    }

    private static void parseSpecialArgument(String token, ArgumentList argumentList) {
        argumentList.specialArguments.add(new SpecialArgument(token.charAt(0), token.substring(1)));
    }

    private static ArrayList<String> getValues(String valueListStr, char separator) {
        ArrayList<String> values = new ArrayList<>();
        if (valueListStr.isEmpty()) {
            return values;
        }

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

    public static class ArgumentListException extends Exception {
        public ArgumentListException(String msg) {
            super(msg);
        }
    }
}
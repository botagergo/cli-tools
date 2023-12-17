package task_manager.cli_lib.argument;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.cli_lib.tokenizer.TokenList;
import task_manager.core.property.Affinity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ArgumentList {
    public ArgumentList(
            @NonNull String commandName,
            @NonNull List<String> leadingNormalArguments,
            @NonNull List<String> trailingNormalArguments,
            @NonNull LinkedHashMap<Character,List<SpecialArgument>> specialArguments,
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

    public ArgumentList() {
        this("", new ArrayList<>(), new ArrayList<>(), new LinkedHashMap<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static ArgumentList from(TokenList tokenList) {
        if (tokenList.tokens().isEmpty()) {
            return new ArgumentList("", new ArrayList<>(), new ArrayList<>(),
                    new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        ArgumentList argList = new ArgumentList();

        for (int i = 0; i < tokenList.tokens().size(); i++) {
            String token = tokenList.tokens().get(i);
            boolean isPropertyArg = false;
            int j;
            for (j = 0; j < token.length(); j++) {
                char currChar = token.charAt(j);
                if ((currChar == '.' || currChar == '+' || currChar == '-')
                        && !tokenList.escapedPositions().contains(Pair.of(i, j))) {
                    isPropertyArg = true;
                }

                if (currChar == ':' && !tokenList.escapedPositions().contains(Pair.of(i, j))) {
                    parsePropertyArgument(i, tokenList, argList, j);
                    break;
                } else if (isPropertyArg && j == token.length() - 1) {
                    parsePropertyArgument(i, tokenList, argList, -1);
                    break;
                }
            }

            if (j >= token.length()) {
                if (argList.commandName.isEmpty()) {
                    if (token.matches("^[a-z]+$")) {
                        argList.commandName = token;
                    } else {
                        argList.leadingNormalArguments.add(token);
                    }
                } else {
                    argList.trailingNormalArguments.add(token);
                }
            }
        }

        return argList;
    }

    private static void parsePropertyArgument(int tokenIndex, TokenList tokenList, ArgumentList argList, int colonIndex) {
        String token = tokenList.tokens().get(tokenIndex);
        int startInd = 0;
        Affinity affinity = Affinity.NEUTRAL;
        boolean isOption = false;
        if (token.charAt(0) == '-') {
            affinity = Affinity.NEGATIVE;
            startInd = 1;
        } else if (token.charAt(0) == '+') {
            affinity = Affinity.POSITIVE;
            startInd = 1;
        } else if (token.charAt(0) == '.') {
            isOption = true;
            startInd = 1;
        }

        String name = colonIndex != -1
                ? token.substring(startInd, colonIndex)
                : token.substring(startInd);

        ArrayList<String> valueList = null;
        if (colonIndex != -1) {
            valueList = getValues(
                    token.substring(colonIndex + 1), ',',
                    tokenList.escapedPositions(),
                    tokenIndex, colonIndex + 1
            );
        }

        if (isOption) {
            argList.optionArguments.add(new OptionArgument(name, valueList));
        } else {
            ArrayList<String> nameList = getValues(
                    name, '.',
                    tokenList.escapedPositions(),
                    tokenIndex, startInd
            );

            String propertyName = "";
            String predicate = null;

            if (!nameList.isEmpty()) {
                propertyName = nameList.get(0);
            }

            if (nameList.size() >= 2) {
                predicate = nameList.get(1);
            }

            if (argList.commandName.isEmpty()) {
                argList.filterPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
            } else {
                argList.modifyPropertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
            }
        }
    }


    private static ArrayList<String> getValues(String valueListStr,
                                               char separator,
                                               Set<Pair<Integer, Integer>> escapedPositions,
                                               int argIndex,
                                               int baseIndex
    ) {
        ArrayList<String> valueList = new ArrayList<>();

        int fromPos = 0;
        int separatorPos = valueListStr.indexOf(separator, fromPos);
        while(separatorPos != -1) {
            if (!escapedPositions.contains(Pair.of(argIndex, baseIndex+separatorPos))) {
                valueList.add(valueListStr.substring(fromPos, separatorPos));
                fromPos = separatorPos+1;
            }
            separatorPos = valueListStr.indexOf(separator, separatorPos+1);
        }
        valueList.add(valueListStr.substring(fromPos));

        return valueList;
    }

    @NonNull private String commandName;
    @NonNull private List<String> leadingNormalArguments;
    @NonNull private List<String> trailingNormalArguments;
    @NonNull private LinkedHashMap<Character, List<SpecialArgument>> specialArguments;
    @NonNull private List<PropertyArgument> filterPropertyArguments;
    @NonNull private List<PropertyArgument> modifyPropertyArguments;
    @NonNull private List<OptionArgument> optionArguments;
}

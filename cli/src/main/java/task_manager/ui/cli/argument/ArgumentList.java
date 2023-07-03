package task_manager.ui.cli.argument;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.core.property.Affinity;
import task_manager.ui.cli.tokenizer.TokenList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ArgumentList {
    public ArgumentList(
            @NonNull String commandName,
            @NonNull List<String> normalArguments,
            @NonNull LinkedHashMap<Character,List<SpecialArgument>> specialArguments,
            @NonNull List<PropertyArgument> propertyArguments,
            @NonNull List<OptionArgument> optionArguments
    ) {
        this.commandName = commandName;
        this.normalArguments = normalArguments;
        this.specialArguments = specialArguments;
        this.propertyArguments = propertyArguments;
        this.optionArguments = optionArguments;
    }

    public ArgumentList() {
        this("", new ArrayList<>(), new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>());
        this.optionArguments = new ArrayList<>();
    }

    public static ArgumentList from(TokenList tokenList) {
        if (tokenList.tokens().isEmpty()) {
            return new ArgumentList("", List.of(),
                    new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>());
        }

        ArgumentList argList = new ArgumentList();

        argList.commandName = tokenList.tokens().get(0);

        for (int i = 1; i < tokenList.tokens().size(); i++) {
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
                argList.normalArguments.add(token);
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

            argList.propertyArguments.add(new PropertyArgument(affinity, propertyName, predicate, valueList));
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

    @Getter @Setter @NonNull private String commandName;
    @Getter @Setter @NonNull private List<String> normalArguments;
    @Getter @Setter @NonNull private LinkedHashMap<Character, List<SpecialArgument>> specialArguments;
    @Getter @Setter @NonNull private List<PropertyArgument> propertyArguments;
    @Getter @Setter @NonNull private List<OptionArgument> optionArguments;
}

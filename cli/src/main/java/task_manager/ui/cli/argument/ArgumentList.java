package task_manager.ui.cli.argument;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.ui.cli.tokenizer.TokenList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ArgumentList {
    public ArgumentList(String commandName, List<String> normalArguments,
            LinkedHashMap<Character, List<SpecialArgument>> specialArguments, List<Pair<String, List<String>>> propertyArguments) {
        this.commandName = commandName;
        this.normalArguments = normalArguments;
        this.specialArguments = specialArguments;
        this.propertyArguments = propertyArguments;
    }

    public ArgumentList() {
        this(null, new ArrayList<>(), new LinkedHashMap<>(), new ArrayList<>());
    }

    public static ArgumentList from(TokenList tokenList) {
        if (tokenList.tokens().isEmpty()) {
            return new ArgumentList(null, List.of(),
                    new LinkedHashMap<>(), new ArrayList<>());
        }

        ArgumentList argList = new ArgumentList();

        argList.commandName = tokenList.tokens().get(0);

        for (int i = 1; i < tokenList.tokens().size(); i++) {
            String token = tokenList.tokens().get(i);
            if (SpecialArgument.isSpecialArgument(token)) {
                try {
                    SpecialArgument specialArg = SpecialArgument.from(token);

                    if (!argList.specialArguments.containsKey(specialArg.type)) {
                        argList.specialArguments.put(specialArg.type, new ArrayList<>());
                    }

                    List<SpecialArgument> specialArgList =
                            argList.specialArguments.get(specialArg.type);
                    specialArgList.add(specialArg);
                } catch (NotASpecialArgumentException e) {
                    throw new RuntimeException("This should not happen");
                }
            } else {
                int j;
                for (j = 0; j < token.length(); j++) {
                    if (token.charAt(j) == ':' && !tokenList.escapedPositions().contains(Pair.of(i, j))) {
                        String name = token.substring(0, j);
                        String value = token.substring(j+1);

                        ArrayList<String> valueList = new ArrayList<>();
                        int fromPos = 0;
                        int colonPos = value.indexOf(',', fromPos);
                        while(colonPos != -1) {
                            if (!tokenList.escapedPositions().contains(Pair.of(i, j+colonPos+1))) {
                                valueList.add(value.substring(fromPos, colonPos));
                                fromPos = colonPos+1;
                            }
                            colonPos = value.indexOf(',', colonPos+1);
                        }
                        valueList.add(value.substring(fromPos));
                        argList.propertyArguments.add(Pair.of(name, valueList));
                        break;
                    }
                }

                if (j >= token.length()) {
                    argList.normalArguments.add(token);
                }
            }
        }

        return argList;
    }

    @Getter @Setter private String commandName;
    @Getter @Setter private List<String> normalArguments;
    @Getter @Setter private LinkedHashMap<Character, List<SpecialArgument>> specialArguments;
    @Getter @Setter private List<Pair<String, List<String>>> propertyArguments;
}

package task_manager.ui.cli.argument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ArgumentList {
    public ArgumentList(String commandName, List<String> normalArguments,
            LinkedHashMap<Character, List<SpecialArgument>> specialArguments) {
        this.commandName = commandName;
        this.normalArguments = normalArguments;
        this.specialArguments = specialArguments;
    }

    public ArgumentList() {
        this(null, new ArrayList<>(), new LinkedHashMap<>());
    }

    public static ArgumentList from(List<String> args) {
        if (args.isEmpty()) {
            return new ArgumentList(null, List.of(),
                    new LinkedHashMap<>());
        }

        ArgumentList argList = new ArgumentList();

        argList.commandName = args.get(0);

        for (String arg : args.subList(1, args.size())) {
            if (SpecialArgument.isSpecialArgument(arg)) {
                try {
                    SpecialArgument specialArg = SpecialArgument.from(arg);

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
                argList.normalArguments.add(arg);
            }
        }

        return argList;
    }

    public String commandName;
    public List<String> normalArguments;
    public LinkedHashMap<Character, List<SpecialArgument>> specialArguments;
}

package task_manager.ui.cli.argument;

import java.util.ArrayList;
import java.util.List;

public class ArgumentList {
    public ArgumentList(String commandName, List<String> normalArguments,
            List<SpecialArgument> specialArguments) {
        this.commandName = commandName;
        this.normalArguments = normalArguments;
        this.specialArguments = specialArguments;
    }

    public ArgumentList() {
        this(null, new ArrayList<>(), new ArrayList<>());
    }

    public static ArgumentList from(List<String> args) {
        if (args.isEmpty()) {
            return new ArgumentList(null, List.of(), List.of());
        }

        ArgumentList argList = new ArgumentList();

        argList.commandName = args.get(0);

        for (String arg : args.subList(1, args.size())) {
            if (SpecialArgument.isSpecialArgument(arg)) {
                try {
                    argList.specialArguments.add(SpecialArgument.from(arg));
                } catch (NotASpecialArgumentException e) {
                    throw new RuntimeException("This should not happen");
                }
            } else {
                argList.normalArguments.add(arg);
            }
        }

        return argList;
    }

    public String commandName = null;
    public List<String> normalArguments;
    public List<SpecialArgument> specialArguments;
}

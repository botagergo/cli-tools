package task_manager.ui.cli.command_parser;

import java.util.ArrayList;
import java.util.List;

public class ArgumentList {
    public ArgumentList(String commandName, List<String> normalArguments, List<String> specialArguments) {
        this.commandName = commandName;
        this.normalArguments = normalArguments;
        this.specialArguments = specialArguments;
    }

    public ArgumentList() {
        this(null, new ArrayList<>(), new ArrayList<>());
    }

    public String commandName = null;
    public List<String> normalArguments;
    public List<String> specialArguments;
}

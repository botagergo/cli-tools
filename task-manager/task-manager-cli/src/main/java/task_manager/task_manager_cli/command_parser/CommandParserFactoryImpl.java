package task_manager.task_manager_cli.command_parser;

import jakarta.inject.Inject;
import task_manager.cli_lib.argument.ArgumentList;
import task_manager.core.repository.ConfigurationRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Inject
    public CommandParserFactoryImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public CommandParser getParser(ArgumentList argList) {
        String commandName = argList.getCommandName();
        String resolvedAlias = configurationRepository.commandAliases().get(commandName);
        if (resolvedAlias != null) {
            commandName = resolvedAlias;
        }

        if (resolvedAlias != null || !configurationRepository.allowCommandPrefix()) {
            Supplier<CommandParser> supplier = commandMapping.get(commandName);
            if (supplier != null) {
                return supplier.get();
            }
        } else {
            List<Map.Entry<String, Supplier<CommandParser>>> matchingCommands = commandMapping.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(argList.getCommandName())).toList();
            if (matchingCommands.size() == 1) {
                return matchingCommands.get(0).getValue().get();
            } else if (matchingCommands.size() > 1) {
                String commandNames = matchingCommands.stream()
                        .map(Map.Entry::getKey).sorted().collect(Collectors.joining(", "));
                System.out.println("Multiple commands match \"" + argList.getCommandName() + "\": " + commandNames);
                return null;
            }
        }

        System.out.println("Unknown command \"" + argList.getCommandName() + "\"");
        return null;
    }

    private final Map<String, Supplier<CommandParser>> commandMapping = Map.of(
            "add", AddTaskCommandParser::new,
            "list", ListTasksCommandParser::new,
            "done", DoneTaskCommandParser::new,
            "clear", ClearCommandParser::new,
            "delete", DeleteTaskCommandParser::new,
            "modify", ModifyTaskCommandParser::new,
            "ai", AICommandParser::new
    );

    private final ConfigurationRepository configurationRepository;

}

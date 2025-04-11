package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.core.util.Print;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandParserFactoryImpl implements CommandParserFactory {

    private final ConfigurationRepository configurationRepository;
    private final Map<String, Supplier<CommandParser>> commandMapping = new java.util.HashMap<>();

    @Inject
    public CommandParserFactoryImpl(
            ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void registerParser(String name, Supplier<CommandParser> commandParserSupplier) {
        this.commandMapping.put(name, commandParserSupplier);
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
                CommandParser commandParser = matchingCommands.get(0).getValue().get();
                if (commandParser == null) {
                    Print.printError("failed to create parser for command '" + argList.getCommandName() + "'");
                    return null;
                }
                return commandParser;
            } else if (matchingCommands.size() > 1) {
                String commandNames = matchingCommands.stream()
                        .map(Map.Entry::getKey).sorted().collect(Collectors.joining(", "));
                Print.printError("multiple commands match '" + argList.getCommandName() + "': " + commandNames);
                return null;
            }
        }


        Print.printError("no such command: '" + argList.getCommandName() + "'");
        return null;
    }

    @Override
    public Collection<String> getCommandNames() {
        return commandMapping.keySet().stream().sorted().toList();
    }
}

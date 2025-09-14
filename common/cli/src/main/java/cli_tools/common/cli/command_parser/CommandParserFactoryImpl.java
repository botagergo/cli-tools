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

        Supplier<CommandParser> supplier = commandMapping.get(commandName);
        if (supplier != null) {
            return supplier.get();
        }

        Print.printError("no such command: '" + argList.getCommandName() + "'");
        return null;
    }

    @Override
    public Collection<String> getCommandNames() {
        return commandMapping.keySet().stream().sorted().toList();
    }
}

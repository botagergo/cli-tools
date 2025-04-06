package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;

import java.util.Collection;
import java.util.function.Supplier;

public interface CommandParserFactory {
    void registerParser(String name, Supplier<CommandParser> commandParserSupplier);
    CommandParser getParser(ArgumentList argList);
    Collection<String> getCommandNames();
}

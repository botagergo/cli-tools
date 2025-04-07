package cli_tools.common.cli.command_line;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command.CommandExecutor;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.tokenizer.MismatchedQuotesException;
import cli_tools.common.cli.tokenizer.Tokenizer;

import java.util.List;

@Getter
public class ExecutorImpl implements Executor {

    public void execute(String commandStr) {
        List<String> tokens;
        try {
            tokens = tokenizer.tokenize(commandStr);
            if (tokens.isEmpty()) {
                return;
            }
        } catch (MismatchedQuotesException e) {
            System.out.println("Syntax error: mismatched quotes");
            return;
        }

        execute(tokens);
    }

    public void execute(List<String> tokens) {
        ArgumentList argList;
        try {
            argList = ArgumentList.from(tokens);
        } catch (ArgumentList.ArgumentListException e) {
            System.out.println("ERROR: " + e);
            return;
        }

        String commandName = argList.getCommandName();
        if (commandName == null || commandName.isEmpty()) {
            System.out.println("no command specified");
            return;
        }

        if (commandName.equals("exit")) {
            _shouldExit = true;
            return;
        }

        CommandParser parser = commandParserFactory.getParser(argList);
        if (parser == null) {
            return;
        }

        try {
            Command command = parser.parse(context, argList);
            commandExecutor.execute(context, command);
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public boolean shouldExit() {
        return _shouldExit;
    }

    private boolean _shouldExit = false;

    @Setter @Inject
    private Tokenizer tokenizer;

    @Setter @Inject
    private CommandParserFactory commandParserFactory;

    @Setter @Inject
    private CommandExecutor commandExecutor;

    @Setter @Inject
    private Context context;

}

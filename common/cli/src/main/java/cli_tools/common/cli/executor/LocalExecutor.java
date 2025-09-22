package cli_tools.common.cli.executor;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command.CommandExecutor;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.tokenizer.MismatchedQuotesException;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.core.util.Print;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Getter
@Log4j2
public class LocalExecutor implements Executor {

    private boolean _shouldExit = false;
    @Setter
    @Inject
    private Tokenizer tokenizer;
    @Setter
    @Inject
    private CommandParserFactory commandParserFactory;
    @Setter
    @Inject
    private CommandExecutor commandExecutor;
    @Setter
    @Inject
    private Context context;

    public void execute(String commandStr) {
        List<String> tokens;
        try {
            tokens = tokenizer.tokenize(commandStr);
            if (tokens.isEmpty()) {
                return;
            }
        } catch (MismatchedQuotesException e) {
            Print.printError("mismatched quotes");
            return;
        }

        execute(tokens);
    }

    private void execute(List<String> tokens) {
        ArgumentList argList;
        try {
            argList = ArgumentList.from(tokens);
        } catch (ArgumentList.ArgumentListException e) {
            Print.printError(e.getMessage());
            return;
        }

        String commandName = argList.getCommandName();
        if (commandName == null || commandName.isEmpty()) {
            Print.printError("no command specified");
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
        } catch (Exception e) {
            Print.printAndLogException(e, log);
        }
    }

    @Override
    public boolean shouldExit() {
        return _shouldExit;
    }

}

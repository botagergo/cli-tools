package task_manager.ui.cli.command_line;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.CommandExecutor;
import task_manager.ui.cli.command_parser.CommandParser;
import task_manager.ui.cli.command_parser.CommandParserException;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.tokenizer.MismatchedQuotesException;
import task_manager.ui.cli.tokenizer.TokenList;
import task_manager.ui.cli.tokenizer.Tokenizer;

@Getter
public class ExecutorImpl implements Executor {

    public void execute(String commandStr) {
        TokenList tokenList;
        try {
            tokenList = tokenizer.tokenize(commandStr);
            if (tokenList.tokens().isEmpty()) {
                return;
            }
        } catch (MismatchedQuotesException e) {
            System.out.println("Syntax error: mismatched quotes");
            return;
        }

        execute(tokenList);
    }

    public void execute(TokenList tokenList) {
        ArgumentList argList = ArgumentList.from(tokenList);
        if (argList.getCommandName().equals("exit")) {
            _shouldExit = true;
            return;
        }

        CommandParser parser = commandParserFactory.getParser(argList);
        if (parser == null) {
            System.out.println("Unknown command: " + argList.getCommandName());
            return;
        }

        try {
            Command command = parser.parse(context, argList);
            commandExecutor.execute(context, command);
        } catch (CommandParserException e) {
            System.out.println("Error parsing command: " + e.getMessage());
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

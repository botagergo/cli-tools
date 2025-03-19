package cli_tools.task_manager.cli.command_line;

import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.Command;
import cli_tools.task_manager.cli.command.CommandExecutor;
import cli_tools.task_manager.cli.command_parser.CommandParser;
import cli_tools.task_manager.cli.command_parser.CommandParserException;
import cli_tools.task_manager.cli.command_parser.CommandParserFactory;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.tokenizer.MismatchedQuotesException;
import cli_tools.common.cli.tokenizer.TokenList;
import cli_tools.common.cli.tokenizer.Tokenizer;

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

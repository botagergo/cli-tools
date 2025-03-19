package common.music_cli.command_line;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import common.cli.argument.ArgumentList;
import common.cli.tokenizer.MismatchedQuotesException;
import common.cli.tokenizer.TokenList;
import common.cli.tokenizer.Tokenizer;
import common.music_cli.Context;
import common.music_cli.command.Command;
import common.music_cli.command.CommandExecutor;
import common.music_cli.command_parser.CommandParser;
import common.music_cli.command_parser.CommandParserFactory;
import common.music_cli.command_parser.CommandParserException;

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

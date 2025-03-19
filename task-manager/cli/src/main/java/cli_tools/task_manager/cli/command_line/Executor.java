package cli_tools.task_manager.cli.command_line;

import cli_tools.common.cli.tokenizer.TokenList;

public interface Executor {

    void execute(String commandStr);

    void execute(TokenList tokenList);

    boolean shouldExit();

}

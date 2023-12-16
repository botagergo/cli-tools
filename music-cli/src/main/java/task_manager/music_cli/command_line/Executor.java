package task_manager.music_cli.command_line;

import task_manager.cli_lib.tokenizer.TokenList;

public interface Executor {

    void execute(String commandStr);

    void execute(TokenList tokenList);

    boolean shouldExit();

}

package task_manager.ui.cli.command_line;

import task_manager.ui.cli.tokenizer.TokenList;

public interface Executor {

    void execute(String commandStr);

    void execute(TokenList tokenList);

}

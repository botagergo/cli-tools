package common.music_cli.command_line;

import common.cli.tokenizer.TokenList;

public interface Executor {

    void execute(String commandStr);

    void execute(TokenList tokenList);

    boolean shouldExit();

}

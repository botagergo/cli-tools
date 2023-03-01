package task_manager.ui.cli;

import task_manager.ui.cli.command_parser.ArgumentList;

public interface ArgumentParser {
    public ArgumentList parse(String str) throws ArgumentParserException;
}

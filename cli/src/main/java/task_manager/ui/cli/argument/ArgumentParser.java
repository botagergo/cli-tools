package task_manager.ui.cli.argument;

public interface ArgumentParser {
    public ArgumentList parse(String str) throws ArgumentParserException;
}

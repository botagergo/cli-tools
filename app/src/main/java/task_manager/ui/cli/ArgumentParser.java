package task_manager.ui.cli;

import java.util.List;

public interface ArgumentParser {
    public List<String> parse(String str) throws ArgumentParserException;
}

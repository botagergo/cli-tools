package cli_tools.task_manager.cli.command_line;

import java.util.List;

public interface Executor {

    void execute(String commandStr);

    void execute(List<String> tokens);

    boolean shouldExit();

}

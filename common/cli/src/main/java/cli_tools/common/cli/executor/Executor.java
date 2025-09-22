package cli_tools.common.cli.executor;

public interface Executor {

    void execute(String commandStr);

    boolean shouldExit();

}

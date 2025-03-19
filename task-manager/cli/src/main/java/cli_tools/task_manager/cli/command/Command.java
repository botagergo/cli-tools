package cli_tools.task_manager.cli.command;

import lombok.Getter;
import lombok.Setter;
import cli_tools.task_manager.cli.Context;

@Setter
@Getter
public abstract class Command {
    public abstract void execute(Context context);
}

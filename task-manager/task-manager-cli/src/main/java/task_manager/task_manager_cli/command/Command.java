package task_manager.task_manager_cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import task_manager.task_manager_cli.Context;

import java.util.List;

@Setter
@Getter
public abstract class Command {
    public abstract void execute(Context context);
}

package cli_tools.common.cli.command;

import cli_tools.common.cli.Context;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Command {
    public abstract void execute(Context context);
}

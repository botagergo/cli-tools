package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;

@Log4j2
@Getter
@Setter
public final class AddLabelCommand extends Command {

    @NonNull private String type;
    @NonNull private List<String> getLabelTexts;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();
        try {
            for (String text : getLabelTexts) {
                if (context.getLabelService().createLabel(type, text)) {
                    Print.printInfo("Created label (%s): %s", type, text);
                } else {
                    Print.printWarning("Label (%s) already exists: %s", type, text);
                }
            }
        } catch (IOException e) {
            Print.printAndLogException(e, log);
        }
    }

}


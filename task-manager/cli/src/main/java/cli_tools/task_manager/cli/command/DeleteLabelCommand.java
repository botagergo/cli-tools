package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
@Setter
public final class DeleteLabelCommand extends Command {

    @NonNull private String type;
    @NonNull private List<String> getLabelTexts;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();
        try {
            for (String text : getLabelTexts) {
                if (context.getLabelService().deleteLabel(type, text)) {
                    Print.printInfo("Deleted label (%s): %s", type, text);
                } else {
                    Print.printWarning("Label (%s) does not exist: %s", type, text);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


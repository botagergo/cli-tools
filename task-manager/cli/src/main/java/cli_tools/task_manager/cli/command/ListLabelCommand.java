package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Getter
@Setter
public final class ListLabelCommand extends Command {

    private List<String> labelTypes;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();
        try {
            if (labelTypes == null || labelTypes.isEmpty()) {
                Map<String, List<String>> allLabels = context.getLabelService().getAllLabels();
                for (Map.Entry<String, List<String>> labels : allLabels.entrySet()) {
                    printLabels(labels.getKey(), labels.getValue());
                }
            } else {
                for (String labelType : labelTypes) {
                    List<String> labels = context.getLabelService().getLabels(labelType);
                    printLabels(labelType, labels);
                }
            }
        } catch (IOException e) {
            Print.printAndLogException(e, log);
        }
    }

    private void printLabels(String labelType, List<String> labels) {
        Print.print(labelType);
        Print.print("-----");
        for (String label : labels) {
            Print.print(label);
        }
        Print.print();
    }

}


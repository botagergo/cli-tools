package cli_tools.task_manager.cli.task_printer;

import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.output_format.OutputFormat;

import java.util.List;

public abstract class TaskPrinter {

    public abstract void printTasks(List<Task> tasks, List<String> properties, TaskManagerContext taskManagerContext)
            throws TaskPrinterException;

    public abstract void printTasksTrees(List<PropertyOwnerTree> taskTrees, List<String> properties, TaskManagerContext taskManagerContext)
            throws TaskPrinterException;

    public static TaskPrinter from(OutputFormat outputFormat) {
        return switch (outputFormat) {
            case OutputFormat.GridOutputFormat gridOutputFormat -> new GridTaskPrinter(
                    gridOutputFormat.intersectChar(),
                    gridOutputFormat.horizontalChar(),
                    gridOutputFormat.verticalChar());
            case OutputFormat.JsonOutputFormat jsonOutputFormat -> new JsonTaskPrinter(jsonOutputFormat.isPretty());
        };
    }

}

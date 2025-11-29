package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.core.data.*;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.task_manager.cli.output_format.OutputFormat;
import cli_tools.task_manager.cli.output_format.OutputFormatRepository;
import cli_tools.task_manager.cli.task_printer.GridTaskPrinter;
import cli_tools.task_manager.cli.task_printer.TaskPrinter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ListTasksCommand extends Command {
    private List<@NonNull SortingCriterion> sortingCriteria;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private String viewName;
    private String outputFormatName;
    private Boolean hierarchical;
    private List<@NonNull Integer> tempIDs;
    private List<String> properties;
    private Boolean overwriteProperties;
    private Boolean listDone;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        TaskManagerContext taskManagerContext = (TaskManagerContext) context;

        try {
            FilterCriterionInfo filterCriterionInfo = null;
            SortingInfo sortingInfo = null;
            String actualViewName = viewName;
            Boolean actualHierarchical = hierarchical;
            List<String> actualProperties = new ArrayList<>(List.of(Task.ID, Task.NAME, Task.STATUS, Task.TAGS));
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);
            Boolean actualListDone = listDone;
            TaskPrinter taskPrinter;

            if (actualViewName == null) {
                actualViewName = context.getConfigurationRepository().defaultView();
            }

            if (actualViewName != null) {
                ViewInfo viewInfo = context.getViewInfoService().getViewInfo(actualViewName);
                if (viewInfo == null) {
                    Print.printError("No such view: %s".formatted(actualViewName));
                    return;
                }

                if (viewInfo.sortingInfo() != null) {
                    sortingInfo = viewInfo.sortingInfo();
                }

                if (viewInfo.filterCriterionInfo() != null) {
                    filterCriterionInfo = viewInfo.filterCriterionInfo();
                }

                if (viewInfo.propertiesToList() != null) {
                    actualProperties = new ArrayList<>(viewInfo.propertiesToList());
                }

                if (outputFormatName == null) {
                    outputFormatName = viewInfo.outputFormat();
                }

                if (actualHierarchical == null) {
                    actualHierarchical = viewInfo.hierarchical();
                }

                if (actualListDone == null) {
                    actualListDone = viewInfo.listDone();
                }
            }

            if (sortingCriteria != null) {
                sortingInfo = new SortingInfo(sortingCriteria);
            }

            if (actualHierarchical == null) {
                actualHierarchical = true;
            }

            if (actualListDone == null) {
                actualListDone = false;
            }

            if (properties != null && !properties.isEmpty()) {
                if (overwriteProperties != null && overwriteProperties) {
                    actualProperties = properties;
                } else {
                    actualProperties.addAll(properties);
                }
            }

            if (actualHierarchical && context.getPropertyManager().getPropertyDescriptor(Task.PARENT) == null) {
                Print.printWarning("Cannot print tasks hierarchically because the 'parent' property does not exist");
                actualHierarchical = false;
            }

            taskPrinter = getTaskPrinter(taskManagerContext.getOutputFormatRepository(), outputFormatName);
            if (taskPrinter == null) {
                return;
            }

            if (actualHierarchical) {
                List<PropertyOwnerTree> taskTrees = taskManagerContext.getTaskService().getTaskTrees(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs, actualListDone);
                taskPrinter.printTasksTrees(taskTrees, actualProperties, taskManagerContext);
            } else {
                List<Task> tasks = taskManagerContext.getTaskService().getTasks(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs, actualListDone);
                taskPrinter.printTasks(tasks, actualProperties, taskManagerContext);

                if (!tempIDs.isEmpty()) {
                    int hiddenTasks = tempIDs.size() - tasks.size();
                    if (hiddenTasks < 0) {
                        throw new RuntimeException();
                    } else if (hiddenTasks >= 1) {
                        Print.printWarning("%d task(s) selected by temporary ID are filtered", hiddenTasks);
                    }
                }
            }
        } catch (Exception e) {
            Print.printAndLogException(e, log);
        }
    }

    private TaskPrinter getTaskPrinter(OutputFormatRepository outputFormatRepository, String outputFormatName) {
        if (outputFormatName == null) {
            return new GridTaskPrinter('+', '-', '|');
        } else {
            OutputFormat outputFormat = outputFormatRepository.get(outputFormatName);
            if (outputFormat == null) {
                Print.printError("No such output format: " + outputFormatName);
                return null;
            }
            return TaskPrinter.from(outputFormat);
        }
    }

}


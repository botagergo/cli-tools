package cli_tools.task_manager.cli.command;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.core.data.*;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ListTasksCommand extends Command {
    private List<@NonNull SortingCriterion> sortingCriteria;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private String viewName;
    private OutputFormat outputFormat;
    private Boolean hierarchical;
    private List<@NonNull Integer> tempIDs;
    private List<String> properties;
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
            List<String> actualProperties = List.of(Task.NAME, Task.STATUS, Task.TAGS);
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);
            Boolean actualListDone = listDone;

            if (actualViewName == null) {
                actualViewName = context.getConfigurationRepository().defaultView();
            }

            if (actualViewName != null) {
                ViewInfo viewInfo = getView(taskManagerContext, actualViewName);
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
                    actualProperties = viewInfo.propertiesToList();
                }

                if (outputFormat == null) {
                    outputFormat = viewInfo.outputFormat();
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

            if (outputFormat == null) {
                outputFormat = OutputFormat.TEXT;
            }

            if (actualHierarchical == null) {
                actualHierarchical = true;
            }

            if (actualListDone == null) {
                actualListDone = false;
            }

            if (properties != null && !properties.isEmpty()) {
                actualProperties = properties;
            }

            if (actualHierarchical && context.getPropertyManager().getPropertyDescriptor(Task.PARENT) == null) {
                Print.printWarning("Cannot print tasks hierarchically because the 'parent' property does not exist");
                actualHierarchical = false;
            }

            if (actualHierarchical) {
                if (outputFormat != OutputFormat.TEXT) {
                    Print.printError("'outputFormat' is not 'text', hierarchical printing is not possible");
                    return;
                }
                List<PropertyOwnerTree> taskHierarchies = taskManagerContext.getTaskService().getTaskTrees(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs, actualListDone);
                taskManagerContext.getTaskPrinter().printTaskTrees(taskManagerContext, taskHierarchies, actualProperties);
            } else {
                List<Task> tasks = taskManagerContext.getTaskService().getTasks(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs, actualListDone);
                taskManagerContext.getTaskPrinter().printTasks(taskManagerContext, tasks, actualProperties, outputFormat);

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

    private ViewInfo getView(TaskManagerContext context, String viewName) throws ServiceException {
        return context.getViewInfoService().getViewInfo(viewName);
    }
}


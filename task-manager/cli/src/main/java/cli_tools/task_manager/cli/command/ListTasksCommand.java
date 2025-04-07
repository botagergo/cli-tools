package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.data.*;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.PropertyOwnerTree;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.task_manager.task.service.TaskServiceException;
import cli_tools.task_manager.cli.TaskManagerContext;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ListTasksCommand extends Command {
    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        TaskManagerContext taskManagerContext = (TaskManagerContext) context;

        try {
            FilterCriterionInfo filterCriterionInfo = null;
            SortingInfo sortingInfo = null;
            String actualViewName = viewName;
            Boolean actualHierarchical = hierarchical;
            List<String> actualProperties = List.of("name", "status", "tags");
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);

            if (actualViewName == null) {
                actualViewName = context.getConfigurationRepository().defaultView();
            }

            if (actualViewName != null) {
                ViewInfo viewInfo = getView(taskManagerContext, actualViewName);

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

            if (properties != null && !properties.isEmpty()) {
                actualProperties = properties;
            }

            if (actualHierarchical && context.getPropertyManager().getPropertyDescriptor("parent") == null) {
                log.warn("cannot print tasks hierarchically because the 'parent' property does not exist");
                actualHierarchical = false;
            }

            if (actualHierarchical) {
                if (outputFormat != OutputFormat.TEXT) {
                    Print.printError("'outputFormat' can only be text when printing tasks hierarchically");
                    return;
                }
                List<PropertyOwnerTree> taskHierarchies = taskManagerContext.getTaskService().getTaskTrees(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
                taskManagerContext.getTaskPrinter().printTaskTrees(taskManagerContext, taskHierarchies, actualProperties);
            } else {
                List<Task> tasks = taskManagerContext.getTaskService().getTasks(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
                taskManagerContext.getTaskPrinter().printTasks(taskManagerContext, tasks, actualProperties, outputFormat);
            }

        } catch (Exception e) {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private @NonNull ViewInfo getView(TaskManagerContext context, String viewName) throws TaskServiceException, IOException {
        ViewInfo viewInfo = context.getViewInfoService().getViewInfo(viewName);
        if (viewInfo == null) {
            throw new TaskServiceException("no such view: '" + viewName + "'");
        }
        return viewInfo;
    }

    private List<@NonNull SortingCriterion> sortingCriteria;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private String viewName;
    private OutputFormat outputFormat;
    private Boolean hierarchical;
    private List<@NonNull Integer> tempIDs;
    private List<String> properties;
}


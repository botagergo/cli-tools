package cli_tools.task_manager.cli.command;

import cli_tools.common.core.data.*;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.TaskHierarchy;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.task_manager.task.service.TaskServiceException;
import cli_tools.task_manager.cli.Context;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ListTasksCommand extends Command {
    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            FilterCriterionInfo filterCriterionInfo = null;
            SortingInfo sortingInfo = null;
            String actualViewName = viewName;
            Boolean actualHierarchical = hierarchical;
            List<String> actualProperties = List.of("name", "status", "tags");
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(context, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(context, filterPropertyArgs);

            if (actualViewName == null) {
                actualViewName = context.getConfigurationRepository().defaultView();
            }

            if (actualViewName != null) {
                ViewInfo viewInfo = getView(context, actualViewName);

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

            if (actualHierarchical) {
                if (outputFormat != OutputFormat.TEXT) {
                    System.out.println("outputFormat can only be text when printing tasks hierarchically");
                    return;
                }
                List<TaskHierarchy> taskHierarchies = context.getTaskService().getTaskHierarchies(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
                context.getTaskPrinter().printTaskHierarchies(context, taskHierarchies, actualProperties);
            } else {
                List<Task> tasks = context.getTaskService().getTasks(filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
                context.getTaskPrinter().printTasks(context, tasks, actualProperties, outputFormat);
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private @NonNull ViewInfo getView(Context context, String viewName) throws TaskServiceException, IOException {
        ViewInfo viewInfo = context.getViewInfoService().getViewInfo(viewName);
        if (viewInfo == null) {
            throw new TaskServiceException("View '" + viewName + "' does not exist");
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


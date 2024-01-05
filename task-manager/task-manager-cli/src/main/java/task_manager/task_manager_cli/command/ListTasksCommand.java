package task_manager.task_manager_cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.core.data.*;
import task_manager.core.property.FilterPropertySpec;
import task_manager.task_logic.use_case.task.TaskUseCaseException;
import task_manager.task_manager_cli.Context;

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
            List<String> propertiesToList = null;
            String actualViewName = viewName;

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
                    propertiesToList = viewInfo.propertiesToList();
                }

                if (viewInfo.outputFormat() != null) {
                    outputFormat = viewInfo.outputFormat();
                }
            }

            if (sortingCriteria != null) {
                sortingInfo = new SortingInfo(sortingCriteria);
            }

            if (propertiesToList == null) {
                propertiesToList = List.of("name", "status", "tags");
            }

            if (outputFormat == null) {
                outputFormat = OutputFormat.TEXT;
            }

            List<Task> tasks = context.getTaskUseCase().getTasks(queries, filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
            context.getTaskPrinter().printTasks(context, tasks, propertiesToList, outputFormat);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private @NonNull ViewInfo getView(Context context, String viewName) throws TaskUseCaseException, IOException {
        ViewInfo viewInfo = context.getViewInfoUseCase().getViewInfo(viewName, context.getPropertyManager());
        if (viewInfo == null) {
            throw new TaskUseCaseException("View '" + viewName + "' does not exist");
        }
        return viewInfo;
    }

    private List<@NonNull String> queries;
    private List<@NonNull SortingCriterion> sortingCriteria;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private String viewName;
    private OutputFormat outputFormat;
    private List<@NonNull Integer> tempIDs;
}


package task_manager.ui.cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.core.data.*;
import task_manager.core.property.FilterPropertySpec;
import task_manager.core.property.PropertyException;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.logic.use_case.task.PropertyConverterException;
import task_manager.logic.use_case.task.TaskUseCaseException;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
public record ListTasksCommand(
        List<@NonNull String> queries,
        List<@NonNull SortingCriterion> sortingCriteria,
        List<@NonNull PropertyArgument> filterPropertyArgs,
        List<@NonNull Integer> tempIDs,
        String viewName
) implements Command {

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
            }

            if (propertiesToList == null) {
                propertiesToList = List.of("name", "status", "tags");
            }

            List<Task> tasks = context.getTaskUseCase().getTasks(queries, filterPropertySpecs, sortingInfo, filterCriterionInfo, taskUUIDs);
            CommandUtil.printTasks(context, tasks, propertiesToList);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private @NonNull ViewInfo getView(Context context, String viewName) throws TaskUseCaseException, IOException {
        try {
            ViewInfo viewInfo = context.getViewInfoUseCase().getViewInfo(viewName, context.getPropertyManager());
            if (viewInfo == null) {
                throw new TaskUseCaseException("View '" + viewName + "' does not exist");
            }
            return viewInfo;
        } catch (PropertyException | PropertyConverterException | FilterCriterionException e) {
            throw new TaskUseCaseException("Failed to get view '" + viewName + "': " + e.getMessage());
        }
    }


}

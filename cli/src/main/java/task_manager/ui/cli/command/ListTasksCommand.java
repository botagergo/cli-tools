package task_manager.ui.cli.command;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import task_manager.core.data.*;
import task_manager.core.property.Property;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertySpec;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.logic.use_case.task.PropertyConverterException;
import task_manager.logic.use_case.task.TaskUseCaseException;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Log4j2
public record ListTasksCommand(
        List<String> queries,
        List<SortingCriterion> sortingCriteria,
        List<PropertyArgument> properties,
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

            List<PropertySpec> propertySpecs = null;
            if (properties != null) {
                propertySpecs = context.getStringToPropertyConverter().convertProperties(properties, false);
            }

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

            List<Task> tasks = context.getTaskUseCase().getTasks(queries, propertySpecs, sortingInfo, filterCriterionInfo);

            if (propertiesToList == null) {
                propertiesToList = List.of("name", "status", "tags");
            }

            SimpleTable table = SimpleTable.of().nextRow()
                    .nextCell().addLine(" ID ");

            for (String propertyName : propertiesToList) {
                    table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
            }

            for (Task task : tasks) {
                int tempID = context.getTempIDMappingRepository().getOrCreateID(task.getUUID());
                addTaskToTable(table, context, task, tempID, propertiesToList);
            }

            GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
            Util.print(gridTable, new PrintWriter(System.out, true));
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}\n{}", e.toString(), ExceptionUtils.getStackTrace(e));
        }
    }

    private void addTaskToTable(SimpleTable table, Context context, Task task, int tempID, List<String> propertiesToList) throws IOException, PropertyException {
        Ansi done;
        if (context.getPropertyManager().getProperty("done", task).getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow()
                .nextCell().addLine(String.format(" %s ", tempID));

        for (String propertyName : propertiesToList) {
            Property property = context.getPropertyManager().getProperty(propertyName, task);
            switch (propertyName) {
                case "name" -> table.nextCell().addLine(String.format(" %s ", done + property.getString()));
                case "done" -> table.nextCell().addLine(String.format(" %s ", property.getBoolean().toString()));
                case "priority" -> table.nextCell().addLine(String.format(" %s ", getLabelStr(context, task, "priority")));
                case "effort" -> table.nextCell().addLine(String.format(" %s ", getLabelStr(context, task, "effort")));
                case "tags" -> table.nextCell().addLine(String.format(" %s ", getTagsStr(context, task)));
                case "status" -> table.nextCell().addLine(String.format(" %s ", getStatusStr(context, task)));
            }
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

    private String getTagsStr(Context context, Task task) throws IOException, PropertyException {
        StringBuilder tagsStr = new StringBuilder();

        LinkedHashSet<UUID> tagUuids = context.getPropertyManager().getProperty("tags", task).getUuidSet();
        for (UUID tagUuid : tagUuids) {
            Label tag = context.getLabelUseCase().getLabel("tag", tagUuid);

            if (tag != null) {
                tagsStr.append("/").append(tag.text()).append(" ");
            }
        }

        return tagsStr.toString();
    }

    private String getStatusStr(Context context, Task task) throws IOException, PropertyException {
        UUID statusUuid = context.getPropertyManager().getProperty("status", task).getUuid();
        if (statusUuid == null) {
            return "";
        }

        Label status = context.getLabelUseCase().getLabel("status", statusUuid);
        if (status == null) {
            log.warn("Status with UUID '" + statusUuid + "' does not exist");
            return "";
        }

        return status.text();
    }

    private String getLabelStr(Context context, Task task, String propertyName) throws IOException, PropertyException {
        Integer value = context.getPropertyManager().getProperty(propertyName, task).getInteger();
        if (value == null) {
            return "";
        }

        OrderedLabel priority = context.getOrderedLabelUseCase().getOrderedLabel(propertyName, value);
        if (priority == null) {
            log.warn("Label with value '" + value + "' does not exist");
            return "";
        }

        return priority.text();
    }

}

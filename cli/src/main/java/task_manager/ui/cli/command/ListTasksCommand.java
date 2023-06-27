package task_manager.ui.cli.command;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import task_manager.core.data.Label;
import task_manager.core.data.OrderedLabel;
import task_manager.core.data.SortingCriterion;
import task_manager.core.data.Task;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertySpec;
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
            List<PropertySpec> propertySpecs = null;
            if (properties != null) {
                propertySpecs = context.getStringToPropertyConverter().convertProperties(properties, false);
            }

            List<Task> tasks = context.getTaskUseCase().getTasks(queries, propertySpecs, sortingCriteria, viewName);

            SimpleTable table = SimpleTable.of().nextRow()
                    .nextCell().addLine("ID")
                    .nextCell().addLine("Name")
                    .nextCell().addLine("Priority")
                    .nextCell().addLine("Effort")
                    .nextCell().addLine("Status")
                    .nextCell().addLine("Tags");

            for (Task task : tasks) {
                int tempID = context.getTempIDMappingRepository().getOrCreateID(task.getUUID());
                addTaskToTable(table, context, task, tempID);
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

    private void addTaskToTable(SimpleTable table, Context context, Task task, int tempID) throws IOException, PropertyException {
        String name = context.getPropertyManager().getProperty("name", task).getString();

        Ansi done;
        if (context.getPropertyManager().getProperty("done", task).getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow()
                .nextCell().addLine(Integer.toString(tempID))
                .nextCell().addLine(done + name)
                .nextCell().addLine(getLabelStr(context, task, "priority"))
                .nextCell().addLine(getLabelStr(context, task, "effort"))
                .nextCell().addLine(getStatusStr(context, task))
                .nextCell().addLine(getTagsStr(context, task));
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

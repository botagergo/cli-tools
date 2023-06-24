package task_manager.ui.cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import task_manager.core.data.Label;
import task_manager.core.data.OrderedLabel;
import task_manager.core.data.SortingCriterion;
import task_manager.core.data.Task;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertySpec;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Log4j2
public record ListTasksCommand(
        List<String> queries, String nameQuery,
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

            List<Task> tasks = context.getTaskUseCase().getTasks(nameQuery, queries, propertySpecs, sortingCriteria, viewName);

            for (Task task : tasks) {
                int tempID = context.getTempIDMappingRepository().getOrCreateID(task.getUUID());
                printTask(context, task, tempID);
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}\n{}", e.toString(), ExceptionUtils.getStackTrace(e));
        }
    }

    private void printTask(Context context, Task task, int tempID) throws IOException, PropertyException {
        String name = context.getPropertyManager().getProperty("name", task).getString();

        Ansi done;
        if (context.getPropertyManager().getProperty("done", task).getBoolean()) {
            done = Ansi.ansi().fg(Color.GREEN).a("✓").reset();
        } else {
            done = Ansi.ansi().a("•");
        }

        System.out.format("%s [%-2d] %-50s\t%-15s\t%-15s\t%s\n", done, tempID, name, getPriorityStr(context, task), getStatusStr(context, task),
                getTagsStr(context, task));
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

    private String getPriorityStr(Context context, Task task) throws IOException, PropertyException {
        Integer priorityInteger = context.getPropertyManager().getProperty("priority", task).getInteger();
        if (priorityInteger == null) {
            return "";
        }

        OrderedLabel priority = context.getOrderedLabelUseCase().getOrderedLabel("priority", priorityInteger);
        if (priority == null) {
            log.warn("Priority with value '" + priorityInteger + "' does not exist");
            return "";
        }

        return priority.text();
    }

}

package task_manager.task_manager_cli.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.fusesource.jansi.Ansi;
import task_manager.cli_lib.DateTimeFormatter;
import task_manager.core.data.*;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyOwner;
import task_manager.task_manager_cli.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Log4j2
public class TaskPrinter {

    @Inject public TaskPrinter() {}

    public void printTasks(Context context, List<Task> tasks,
                           List<String> propertiesToList,
                           OutputFormat outputFormat) throws PropertyException, IOException {
        if (outputFormat.equals(OutputFormat.TEXT)) {
            printTasksText(context, tasks, propertiesToList);
        } else if (outputFormat.equals(OutputFormat.JSON)) {
            System.out.println(getObjectMapper().writeValueAsString(tasks));
        } else if (outputFormat.equals(OutputFormat.PRETTY_JSON)) {
            System.out.println(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tasks));
        }
    }

    public void printTaskHierarchies(Context context, List<TaskHierarchy> taskHierarchies,
                                     List<String> propertiesToList) throws PropertyException, IOException {
        SimpleTable table = SimpleTable.of().nextRow();

        for (String propertyName : propertiesToList) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (TaskHierarchy taskHierarchy : taskHierarchies) {
            addTaskHierarchyToTable(table, context, taskHierarchy, propertiesToList, 0);
        }

        GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    private void printTasksText(Context context,
                                List<Task> tasks,
                                List<String> propertiesToList) throws PropertyException, IOException {
        SimpleTable table = SimpleTable.of().nextRow();

        for (String propertyName : propertiesToList) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (Task task : tasks) {
            addTaskToTable(table, context, task, propertiesToList);
        }

        GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    private void addTaskHierarchyToTable(SimpleTable table, Context context, TaskHierarchy taskHierarchy, List<String> propertiesToList, int depth) throws IOException, PropertyException {
        Ansi done;
        if (context.getPropertyManager().getProperty(taskHierarchy, "done").getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow();

        for (int i = 0; i < propertiesToList.size(); i++) {
            String content;
            String propertyName = propertiesToList.get(i);
            Property property = context.getPropertyManager().getProperty(taskHierarchy, propertyName);
            switch (propertyName) {
                case "name" -> content = String.format(" %s ", done + property.getString());
                case "done" -> content = String.format(" %s ", property.getBoolean().toString());
                case "priority" -> content = String.format(" %s ", getLabelStr(context, taskHierarchy, "priority"));
                case "effort" -> content = String.format(" %s ", getLabelStr(context, taskHierarchy, "effort"));
                case "tags" -> content = String.format(" %s ", getTagsStr(context, taskHierarchy));
                case "status" -> content = String.format(" %s ", getStatusStr(context, taskHierarchy));
                case "id" -> content = String.format(" %s ", getIDStr(context, taskHierarchy));
                case "startDate" -> content = String.format(" %s ", getDateStr(context.getPropertyManager().getProperty(taskHierarchy, "startDate").getDate()));
                case "startTime" -> content = String.format(" %s ", getTimeStr(context.getPropertyManager().getProperty(taskHierarchy, "startTime").getTime()));
                case "dueDate" -> content = String.format(" %s ", getDateStr(context.getPropertyManager().getProperty(taskHierarchy, "dueDate").getDate()));
                case "dueTime" -> content = String.format(" %s ", getTimeStr(context.getPropertyManager().getProperty(taskHierarchy, "dueTime").getTime()));
                case "uuid" -> content = String.format(" %s ", property.getUuid());
                default -> throw new RuntimeException();
            }

            if (i == 0 && depth > 0) {
                content = " •".repeat(depth) + content;
            }

            table.nextCell().addLine(content);
        }

        if (taskHierarchy.getChildren() != null) {
            for (TaskHierarchy child : taskHierarchy.getChildren()) {
                addTaskHierarchyToTable(table, context, child, propertiesToList, depth+1);
            }
        }
    }

    private void addTaskToTable(SimpleTable table, Context context, Task task, List<String> propertiesToList) throws IOException, PropertyException {
        Ansi done;
        if (context.getPropertyManager().getProperty(task, "done").getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow();

        for (String propertyName : propertiesToList) {
            Property property = context.getPropertyManager().getProperty(task, propertyName);
            switch (propertyName) {
                case "name" -> table.nextCell().addLine(String.format(" %s ", done + property.getString()));
                case "done" -> table.nextCell().addLine(String.format(" %s ", property.getBoolean().toString()));
                case "priority" -> table.nextCell().addLine(String.format(" %s ", getLabelStr(context, task, "priority")));
                case "effort" -> table.nextCell().addLine(String.format(" %s ", getLabelStr(context, task, "effort")));
                case "tags" -> table.nextCell().addLine(String.format(" %s ", getTagsStr(context, task)));
                case "status" -> table.nextCell().addLine(String.format(" %s ", getStatusStr(context, task)));
                case "id" -> table.nextCell().addLine(String.format(" %s ", getIDStr(context, task)));
                case "startDate" -> table.nextCell().addLine(String.format(" %s ", getDateStr(context.getPropertyManager().getProperty(task, "startDate").getDate())));
                case "startTime" -> table.nextCell().addLine(String.format(" %s ", getTimeStr(context.getPropertyManager().getProperty(task, "startTime").getTime())));
                case "dueDate" -> table.nextCell().addLine(String.format(" %s ", getDateStr(context.getPropertyManager().getProperty(task, "dueDate").getDate())));
                case "dueTime" -> table.nextCell().addLine(String.format(" %s ", getTimeStr(context.getPropertyManager().getProperty(task, "dueTime").getTime())));
                case "uuid" -> table.nextCell().addLine(String.format(" %s ", property.getUuid()));
                default -> throw new RuntimeException();
            }
        }
    }

    private String getTagsStr(Context context, PropertyOwner task) throws IOException, PropertyException {
        StringBuilder tagsStr = new StringBuilder();

        Set<UUID> tagUuids = context.getPropertyManager().getProperty(task, "tags").getUuidSet();
        for (UUID tagUuid : tagUuids) {
            Label tag = context.getLabelUseCase().getLabel("tag", tagUuid);

            if (tag != null) {
                tagsStr.append("/").append(tag.text()).append(" ");
            }
        }

        return tagsStr.toString();
    }

    private String getStatusStr(Context context, PropertyOwner propertyOwner) throws IOException, PropertyException {
        UUID statusUuid = context.getPropertyManager().getProperty(propertyOwner, "status").getUuid();
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

    private String getLabelStr(Context context, PropertyOwner propertyOwner, String propertyName) throws IOException, PropertyException {
        Integer value = context.getPropertyManager().getProperty(propertyOwner, propertyName).getInteger();
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

    private String getIDStr(Context context, PropertyOwner propertyOwner) throws PropertyException, IOException {
        return context.getPropertyManager().getProperty(propertyOwner, "id").getInteger().toString();
    }

    private String getDateStr(LocalDate localDate) {
        return localDate != null ? dateTimeFormatter.formatLocalDate(localDate) : "";
    }

    private String getTimeStr(LocalTime localTime) {
        return localTime != null ? dateTimeFormatter.formatLocalTime(localTime) : "";
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    private final DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
    private ObjectMapper objectMapper = null;

}

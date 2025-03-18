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
import task_manager.cli_lib.property_to_string_converter.PropertyToStringConverter;
import task_manager.core.data.*;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyOwner;
import task_manager.task_manager_cli.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Log4j2
public class TaskPrinter {

    @Inject public TaskPrinter() {}

    public void printTasks(Context context, List<Task> tasks,
                           List<String> propertiesToList,
                           OutputFormat outputFormat) throws PropertyException, IOException {

        PropertyToStringConverter propertyToStringConverter = new PropertyToStringConverter(
                context.getLabelUseCase(),
                context.getOrderedLabelUseCase(),
                new DateTimeFormatter());

        if (outputFormat.equals(OutputFormat.TEXT)) {
            printTasksText(context, propertyToStringConverter, tasks, propertiesToList);
        } else if (outputFormat.equals(OutputFormat.JSON)) {
            System.out.println(getObjectMapper().writeValueAsString(tasks));
        } else if (outputFormat.equals(OutputFormat.PRETTY_JSON)) {
            System.out.println(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tasks));
        }
    }

    public void printTaskHierarchies(
            Context context,
            List<TaskHierarchy> taskHierarchies,
            List<String> propertiesToList) throws PropertyException, IOException {
        SimpleTable table = SimpleTable.of().nextRow();

        PropertyToStringConverter propertyToStringConverter = new PropertyToStringConverter(
                context.getLabelUseCase(),
                context.getOrderedLabelUseCase(),
                new DateTimeFormatter());

        for (String propertyName : propertiesToList) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (TaskHierarchy taskHierarchy : taskHierarchies) {
            addTaskHierarchyToTable(table, context, propertyToStringConverter, taskHierarchy, propertiesToList, 0);
        }

        GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    private void printTasksText(Context context,
                                PropertyToStringConverter propertyToStringConverter,
                                List<Task> tasks,
                                List<String> propertiesToList) throws PropertyException, IOException {
        SimpleTable table = SimpleTable.of().nextRow();

        for (String propertyName : propertiesToList) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (Task task : tasks) {
            addTaskToTable(table, context, propertyToStringConverter, task, propertiesToList);
        }

        GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    private void addTaskHierarchyToTable(
            SimpleTable table,
            Context context,
            PropertyToStringConverter propertyToStringConverter,
            TaskHierarchy taskHierarchy,
            List<String> propertiesToList,
            int depth) throws IOException, PropertyException {
        Ansi done;
        if (context.getPropertyManager().getProperty(taskHierarchy, "done").getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow();

        for (int i = 0; i < propertiesToList.size(); i++) {
            String propertyName = propertiesToList.get(i);
            Property property = context.getPropertyManager().getProperty(taskHierarchy, propertyName);
            String propertyString;

            if (propertyName.equals("id")) {
                propertyString = getIDStr(context, taskHierarchy);
            } else {
                propertyString = propertyToStringConverter.propertyToString(property);
            }

            if (propertyName.equals("name")) {
                propertyString = done + propertyString;
            }

            if (i == 0 && depth > 0) {
                propertyString = " •".repeat(depth) + propertyString;
            }

            table.nextCell().addLine(" " + propertyString + " ");
        }

        if (taskHierarchy.getChildren() != null) {
            for (TaskHierarchy child : taskHierarchy.getChildren()) {
                addTaskHierarchyToTable(table, context, propertyToStringConverter, child, propertiesToList, depth+1);
            }
        }
    }

    private void addTaskToTable(
            SimpleTable table,
            Context context,
            PropertyToStringConverter propertyToStringConverter,
            Task task,
            List<String> propertiesToList) throws IOException, PropertyException {
        Ansi done;
        if (context.getPropertyManager().getProperty(task, "done").getBoolean()) {
            done = Ansi.ansi().a("✓ ");
        } else {
            done = Ansi.ansi().a("");
        }

        table.nextRow();

        for (String propertyName : propertiesToList) {
            Property property = context.getPropertyManager().getProperty(task, propertyName);
            String propertyString;

            if (propertyName.equals("id")) {
                propertyString = getIDStr(context, task);
            } else {
                propertyString = propertyToStringConverter.propertyToString(property);
            }

            if (propertyName.equals("name")) {
                propertyString = done + propertyString;
            }

            table.nextCell().addLine(String.format(" %s ", propertyString));
        }
    }

    private String getIDStr(Context context, PropertyOwner propertyOwner) throws PropertyException, IOException {
        return context.getPropertyManager().getProperty(propertyOwner, "id").getInteger().toString();
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    private ObjectMapper objectMapper = null;

}

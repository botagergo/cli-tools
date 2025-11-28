package cli_tools.task_manager.cli.command;

import cli_tools.common.backend.property_converter.PropertyConverterException;
import cli_tools.common.cli.Context;
import cli_tools.common.cli.DateTimeFormatter;
import cli_tools.common.cli.property_to_string_converter.DefaultPropertyToStringConverter;
import cli_tools.common.cli.property_to_string_converter.MainPropertyToStringConverter;
import cli_tools.common.cli.property_to_string_converter.PropertyToStringConverter;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.util.Print;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import lombok.extern.log4j.Log4j2;
import org.fusesource.jansi.Ansi;

import java.io.PrintWriter;
import java.util.List;

@Log4j2
public class TaskPrinter {

    private ObjectMapper objectMapper = null;

    public TaskPrinter() {
    }

    public void printTasks(TaskManagerContext context, List<Task> tasks,
                           List<String> propertiesToList,
                           OutputFormat outputFormat) throws TaskPrinterException {

        DefaultPropertyToStringConverter propertyToStringConverter = new DefaultPropertyToStringConverter(
                context.getLabelService(),
                context.getOrderedLabelService(),
                new DateTimeFormatter());
        MainPropertyToStringConverter mainPropertyToStringConverter = new MainPropertyToStringConverter(context.getPropertyToStringConverterRepository(), propertyToStringConverter);

        try {
            if (outputFormat.equals(OutputFormat.TEXT)) {
                printTasksText(context, mainPropertyToStringConverter, tasks, propertiesToList);
            } else if (outputFormat.equals(OutputFormat.JSON)) {
                Print.print(getObjectMapper().writeValueAsString(tasks.stream().map(Task::getProperties).toList()));
            } else if (outputFormat.equals(OutputFormat.PRETTY_JSON)) {
                Print.print(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tasks.stream().map(Task::getProperties).toList()));
            }
        } catch (PropertyConverterException | JsonProcessingException | PropertyException e) {
            throw new TaskPrinterException("Error printing tasks: %s".formatted(e.getMessage()), e);
        }
    }

    public void printTaskTrees(
            Context context,
            List<PropertyOwnerTree> taskTrees,
            List<String> propertiesToList) throws PropertyException, PropertyConverterException {
        SimpleTable table = SimpleTable.of().nextRow();

        DefaultPropertyToStringConverter propertyToStringConverter = new DefaultPropertyToStringConverter(
                context.getLabelService(),
                context.getOrderedLabelService(),
                new DateTimeFormatter());
        MainPropertyToStringConverter mainPropertyToStringConverter = new MainPropertyToStringConverter(context.getPropertyToStringConverterRepository(), propertyToStringConverter);

        for (String propertyName : propertiesToList) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (PropertyOwnerTree taskTree : taskTrees) {
            addTaskTreeToTable(table, context, mainPropertyToStringConverter, taskTree, propertiesToList, 0);
        }

        GridTable gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    private void printTasksText(TaskManagerContext context,
                                PropertyToStringConverter propertyToStringConverter,
                                List<Task> tasks,
                                List<String> propertiesToList) throws PropertyException, PropertyConverterException {
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

    private void addTaskTreeToTable(
            SimpleTable table,
            cli_tools.common.cli.Context context,
            PropertyToStringConverter propertyToStringConverter,
            PropertyOwnerTree taskTree,
            List<String> propertiesToList,
            int depth) throws PropertyException, PropertyConverterException {
        Ansi ansiDone;
        Boolean done = context.getPropertyManager().getProperty(taskTree, "done").getBoolean();
        if (done != null && done) {
            ansiDone = Ansi.ansi().a("✓ ");
        } else {
            ansiDone = Ansi.ansi().a("");
        }

        table.nextRow();

        for (int i = 0; i < propertiesToList.size(); i++) {
            String propertyName = propertiesToList.get(i);
            Property property = context.getPropertyManager().getProperty(taskTree, propertyName);
            String propertyString;

            if (propertyName.equals("id")) {
                propertyString = getIDStr(context, taskTree);
            } else {
                propertyString = propertyToStringConverter.propertyToString(propertyName, property);
            }

            if (propertyName.equals(Task.NAME

)) {
                propertyString = ansiDone + propertyString;
            }

            if (i == 0 && depth > 0) {
                propertyString = " •".repeat(depth) + " " + propertyString;
            }

            table.nextCell();
            for (String line : splitByNewlines(propertyString)) {
                table.addLine(" " + line + " ");
            }
        }

        if (taskTree.getChildren() != null) {
            for (PropertyOwnerTree child : taskTree.getChildren()) {
                addTaskTreeToTable(table, context, propertyToStringConverter, child, propertiesToList, depth + 1);
            }
        }
    }

    private void addTaskToTable(
            SimpleTable table,
            TaskManagerContext context,
            PropertyToStringConverter propertyToStringConverter,
            Task task,
            List<String> propertiesToList) throws PropertyException, PropertyConverterException {
        Ansi ansiDone;
        Boolean done = context.getPropertyManager().getProperty(task, "done").getBoolean();
        if (done != null && done) {
            ansiDone = Ansi.ansi().a("✓ ");
        } else {
            ansiDone = Ansi.ansi().a("");
        }

        table.nextRow();

        for (String propertyName : propertiesToList) {
            Property property = context.getPropertyManager().getProperty(task, propertyName);
            String propertyString;

            if (propertyName.equals("id")) {
                propertyString = getIDStr(context, task);
            } else {
                propertyString = propertyToStringConverter.propertyToString(propertyName, property);
            }

            if (propertyName.equals(Task.NAME

)) {
                propertyString = ansiDone + propertyString;
            }

            table.nextCell();
            for (String line : splitByNewlines(propertyString)) {
                table.addLine(" " + line + " ");
            }
        }
    }

    private String getIDStr(Context context, PropertyOwner propertyOwner) throws PropertyException {
        return context.getPropertyManager().getProperty(propertyOwner, "id").getInteger().toString();
    }

    private String[] splitByNewlines(String str) {
        return str.split("\\r?\\n");
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

}

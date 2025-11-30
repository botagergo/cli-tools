package cli_tools.task_manager.cli.task_printer;

import cli_tools.common.backend.property_converter.PropertyConverterException;
import cli_tools.common.cli.property_to_string_converter.PropertyToStringConverter;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.task_manager.cli.TaskManagerContext;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import lombok.AllArgsConstructor;
import org.fusesource.jansi.Ansi;

import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class GridTaskPrinter extends TaskPrinter {

    private char intersectChar;
    private char horizontalChar;
    private char verticalChar;

    @Override
    public void printTasks(List<Task> tasks,
                            List<String> properties,
                           TaskManagerContext taskManagerContext) throws TaskPrinterException {
        SimpleTable table = SimpleTable.of().nextRow();

        for (String propertyName : properties) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (Task task : tasks) {
            try {
                addTaskToGrid(table, taskManagerContext, taskManagerContext.getPropertyToStringConverter(), task, properties);
            } catch (PropertyException | PropertyConverterException e) {
                throw new TaskPrinterException(e.getMessage(), e);
            }
        }

        GridTable gridTable = Border.of(Border.Chars.of(intersectChar, horizontalChar, verticalChar)).apply(table.toGrid());
        Util.print(gridTable, new PrintWriter(System.out, true));
    }

    @Override
    public void printTasksTrees(List<PropertyOwnerTree> taskTrees, List<String> properties, TaskManagerContext taskManagerContext) throws TaskPrinterException {
        SimpleTable table = SimpleTable.of().nextRow();

        for (String propertyName : properties) {
            table.nextCell().addLine(String.format(" %s ", propertyName.toUpperCase()));
        }

        for (PropertyOwnerTree taskTree : taskTrees) {
            try {
                addTaskTreeToTable(table, taskManagerContext, taskManagerContext.getPropertyToStringConverter(), taskTree, properties, 0);
            } catch (PropertyException | PropertyConverterException e) {
                throw new TaskPrinterException(e.getMessage(), e);
            }
        }

        GridTable gridTable = Border.of(Border.Chars.of(intersectChar, horizontalChar, verticalChar)).apply(table.toGrid());
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
            String propertyString = propertyToStringConverter.propertyToString(propertyName, property);

            if (propertyName.equals(Task.NAME)) {
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

    private void addTaskToGrid(
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

            propertyString = propertyToStringConverter.propertyToString(propertyName, property);

            if (propertyName.equals(Task.NAME)) {
                propertyString = ansiDone + propertyString;
            }

            table.nextCell();
            for (String line : splitByNewlines(propertyString)) {
                table.addLine(" " + line + " ");
            }
        }
    }

    private String[] splitByNewlines(String str) {
        return str.split("\\r?\\n");
    }

}

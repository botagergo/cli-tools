package common.music_cli.command;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.fusesource.jansi.Ansi;
import common.core.data.Label;
import common.core.data.OrderedLabel;
import common.core.data.Task;
import common.core.property.FilterPropertySpec;
import common.property_lib.Property;
import common.property_lib.PropertyException;
import common.property_lib.PropertyManager;
import common.music_cli.Context;
import common.cli.argument.PropertyArgument;
import common.music_cli.command.string_to_property_converter.StringToPropertyConverterException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Log4j2
public class CommandUtil {
    public static List<UUID> getUUIDsFromTempIDs(@NonNull Context context, List<Integer> tempIDs) throws IOException {
        if (tempIDs != null && !tempIDs.isEmpty()) {
            List<UUID> taskUUIDs = new ArrayList<>();
            for (int tempID : tempIDs) {
                UUID uuid = context.getTempIDMappingUseCase().getUUID(tempID);
                taskUUIDs.add(uuid);
            }
            return taskUUIDs;
        } else {
            return null;
        }
    }

    public static List<FilterPropertySpec> getFilterPropertySpecs(@NonNull Context context, List<PropertyArgument> filterPropertyArgs) throws StringToPropertyConverterException, PropertyException, IOException {
        if (filterPropertyArgs != null && !filterPropertyArgs.isEmpty()) {
            return context.getStringToPropertyConverter().convertPropertiesForFiltering(filterPropertyArgs, false);
        } else {
            return null;
        }
    }

    public static List<Task> confirmAndGetTasksToChange(
            @NonNull Context context,
            @NonNull List<Task> tasks,
            List<Integer> tempIDs,
            List<FilterPropertySpec> filterPropertySpecs,
            @NonNull ChangeType changeType
    ) throws PropertyException, IOException {
        // No need for confirmation if temporary IDs were specified,
        // or if there is only one matching task
        if (tempIDs != null || tasks.size() < 2) {
            return tasks;
        }

        String message = getMessageFromChangeType(tasks, filterPropertySpecs, changeType);

        char answer = prompt(message, "ynsp");
        switch (answer) {
            case 'y' -> {
                return tasks;
            }
            case 'n' -> {
                return null;
            }
            case 's' -> {
                printTasks(context, tasks, List.of("name"));
                return confirmAndGetTasksToChange(context, tasks, null, filterPropertySpecs, changeType);
            }
            case 'p' -> {
                return pickTasks(context.getPropertyManager(), tasks, changeType);
            }
            default -> throw new RuntimeException();
        }
    }

    private static String getMessageFromChangeType(List<Task> tasks, List<FilterPropertySpec> filterPropertySpecs, ChangeType changeType) {
        String message;
        if (filterPropertySpecs == null) {
            message = switch (changeType) {
                case DELETE ->
                        "No filter was specified, delete all (" + tasks.size() + ") tasks? ([y]es/[n]o/[s]how/[p]ick) ";
                case DONE ->
                        "No filter was specified, mark all (" + tasks.size() + ") tasks as done? ([y]es/[n]o/[s]how/[p]ick) ";
                case MODIFY ->
                        "No filter was specified, modify all (" + tasks.size() + ") tasks? ([y]es/[n]o/[s]how/[p]ick) ";
            };
        } else {
            message = switch (changeType) {
                case DELETE -> "Deleting " + tasks.size() + " tasks. Continue? ([y]es/[n]o/[s]how/[p]ick) ";
                case DONE -> "Marking " + tasks.size() + " tasks as done. Continue? ([y]es/[n]o/[s]how/[p]ick) ";
                case MODIFY -> "Modifying " + tasks.size() + " tasks. Continue? ([y]es/[n]o/[s]how/[p]ick) ";
            };
        }
        return message;
    }

    private static List<Task> pickTasks(PropertyManager propertyManager, List<Task> tasks, ChangeType changeType) throws PropertyException, IOException {
        List<Task> tasksToChange = new ArrayList<>();

        for (Task task : tasks) {
            String taskName = propertyManager.getProperty(task, "name").getString();

            String message = switch (changeType) {
                case DELETE -> "Delete task '" + taskName + "'? ([y]es/[n]o/[c]ancel/[q]uit) ";
                case DONE -> "Mark task '" + taskName + "' as done? ([y]es/[n]o/[c]ancel/[q]uit) ";
                case MODIFY -> "Modify task '" + taskName + "'? ([y]es/[n]o/[c]ancel/[q]uit) ";
            };

            char answer = prompt(message, "yncq");
            if (answer == 'y') {
                tasksToChange.add(task);
            } else if (answer == 'q') {
                return tasksToChange;
            } else if (answer == 'c') {
                return null;
            }
        }

        return tasksToChange;
    }

    public static void printTasks(Context context, List<Task> tasks, List<String> propertiesToList) throws IOException, PropertyException {
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

    private static void addTaskToTable(SimpleTable table, Context context, Task task, List<String> propertiesToList) throws IOException, PropertyException {
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
                default -> throw new RuntimeException();
            }
        }
    }

    private static String getTagsStr(Context context, Task task) throws IOException, PropertyException {
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

    private static String getStatusStr(Context context, Task task) throws IOException, PropertyException {
        UUID statusUuid = context.getPropertyManager().getProperty(task, "status").getUuid();
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

    private static String getLabelStr(Context context, Task task, String propertyName) throws IOException, PropertyException {
        Integer value = context.getPropertyManager().getProperty(task, propertyName).getInteger();
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

    private static String getIDStr(Context context, Task task) throws PropertyException, IOException {
        return context.getPropertyManager().getProperty(task, "id").getInteger().toString();
    }

    public static char prompt(String message, String options) {
        while (true) {
            System.out.print(message);
            String answer = scanner.nextLine().toLowerCase();
            if (answer.length() == 1 && options.indexOf(answer.charAt(0)) != -1) {
                return answer.charAt(0);
            }
        }
    }

    public enum ChangeType {
        MODIFY,
        DONE,
        DELETE
    }

    private static final Scanner scanner = new Scanner(System.in);
}

package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.string_to_property_converter.StringToPropertyConverterException;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.task.Task;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
public class CommandUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static List<UUID> getUUIDsFromTempIDs(@NonNull TaskManagerContext context, List<Integer> tempIDs) throws IOException {
        if (tempIDs == null || tempIDs.isEmpty()) {
            return null;
        }

        List<UUID> uuids = new ArrayList<>();
        List<Integer> nonexistentTempIds = new ArrayList<>();

        for (int tempID : tempIDs) {
            UUID uuid = context.getTempIdManager().getUUID(tempID);
            if (uuid == null) {
                nonexistentTempIds.add(tempID);
            } else {
                uuids.add(uuid);
            }
        }

        if (!nonexistentTempIds.isEmpty()) {
            throw new IllegalArgumentException("no task with temporary ID: %s".formatted(
                    nonexistentTempIds.stream().map(Object::toString).collect(Collectors.joining(", "))));
        }

        return uuids;
    }

    public static List<FilterPropertySpec> getFilterPropertySpecs(@NonNull TaskManagerContext context, List<PropertyArgument> filterPropertyArgs) throws StringToPropertyConverterException, PropertyException, IOException {
        if (filterPropertyArgs != null && !filterPropertyArgs.isEmpty()) {
            return context.getStringToPropertyConverter().convertPropertiesForFiltering(filterPropertyArgs, false);
        } else {
            return null;
        }
    }

    public static List<Task> confirmAndGetTasksToChange(
            @NonNull TaskManagerContext context,
            @NonNull List<Task> tasks,
            List<Integer> tempIDs,
            List<FilterPropertySpec> filterPropertySpecs,
            @NonNull ChangeType changeType
    ) throws PropertyException, IOException {
        if (context.getConfigurationRepository().disableConfirmation() || tempIDs != null && !tempIDs.isEmpty()) {
            return tasks;
        }

        String message = getMessageFromChangeType(tasks, tempIDs, filterPropertySpecs, changeType);

        char answer = prompt(message, "ynsp");
        switch (answer) {
            case 'y' -> {
                return tasks;
            }
            case 'n' -> {
                return null;
            }
            case 's' -> {
                context.getTaskPrinter().printTasks(context, tasks, List.of("name"), OutputFormat.TEXT);
                return confirmAndGetTasksToChange(context, tasks, null, filterPropertySpecs, changeType);
            }
            case 'p' -> {
                return pickTasks(context.getPropertyManager(), tasks, changeType);
            }
            default -> throw new RuntimeException();
        }
    }

    private static String getMessageFromChangeType(
            List<Task> tasks,
            List<Integer> tempIDs,
            List<FilterPropertySpec> filterPropertySpecs,
            ChangeType changeType) {
        String message;
        String taskStr = tasks.size() > 1 ? "tasks" : "task";
        if ((filterPropertySpecs == null || filterPropertySpecs.isEmpty())
                && (tempIDs == null || tempIDs.isEmpty())) {
            message = switch (changeType) {
                case DELETE ->
                        "No filter was specified, delete all (" + tasks.size() + ") " + taskStr + "? ([y]es/[n]o/[s]how/[p]ick) ";
                case DONE ->
                        "No filter was specified, mark all (" + tasks.size() + ") " + taskStr + " as done? ([y]es/[n]o/[s]how/[p]ick) ";
                case MODIFY ->
                        "No filter was specified, modify all (" + tasks.size() + ") " + taskStr + "? ([y]es/[n]o/[s]how/[p]ick) ";
            };
        } else {
            message = switch (changeType) {
                case DELETE -> "Deleting " + tasks.size() + " " + taskStr + ". Continue? ([y]es/[n]o/[s]how/[p]ick) ";
                case DONE ->
                        "Marking " + tasks.size() + " " + taskStr + " as done. Continue? ([y]es/[n]o/[s]how/[p]ick) ";
                case MODIFY -> "Modifying " + tasks.size() + " " + taskStr + ". Continue? ([y]es/[n]o/[s]how/[p]ick) ";
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
}

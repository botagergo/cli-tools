package task_manager.task_manager_cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.core.property.ModifyPropertySpec;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.task_manager_cli.Context;
import task_manager.cli_lib.property_modifier.PropertyModifier;
import task_manager.cli_lib.string_to_property_converter.StringToPropertyConverterException;

import java.util.List;
import java.util.UUID;

@Log4j2
public record ModifyTaskCommand(
        List<@NonNull Integer> tempIDs,
        List<@NonNull PropertyArgument> filterPropertyArgs,
        List<@NonNull PropertyArgument> modifyPropertyArgs
) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(context, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(context, filterPropertyArgs);

            List<Task> tasks = context.getTaskUseCase().getTasks(
                    null, filterPropertySpecs, null, null, taskUUIDs);

            tasks = CommandUtil.confirmAndGetTasksToChange(context, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.MODIFY);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }

            for (Task task : tasks) {
                if (modifyPropertyArgs != null) {
                    List<ModifyPropertySpec> modifyPropertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                    PropertyModifier.modifyProperties(context.getPropertyManager(), task, modifyPropertySpecs);
                }
                Task modifiedTask = context.getTaskUseCase().modifyTask(task);
                 if (tasks.size() == 1) {
                    int tempID = context.getTempIDMappingUseCase().getOrCreateID(modifiedTask.getUUID());
                    context.setPrevTaskID(tempID);
                }
            }

        } catch (StringToPropertyConverterException e) {
            switch (e.getExceptionType()) {
                case NotAList -> System.out.println("A list of values was provided, but property '" + e.getArgument() + "' is not a list");
                case EmptyList -> System.out.println("No value was provided for property '" + e.getArgument() + "'");
                case LabelNotFound -> System.out.println("No changes were made");
                case OrderedLabelNotFound -> System.out.println("Label not found: " + e.getArgument());
                default -> System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

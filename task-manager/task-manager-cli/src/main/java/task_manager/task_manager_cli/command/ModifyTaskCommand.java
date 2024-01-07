package task_manager.task_manager_cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.cli_lib.property_modifier.PropertyModifier;
import task_manager.cli_lib.string_to_property_converter.StringToPropertyConverterException;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.core.property.ModifyPropertySpec;
import task_manager.task_manager_cli.Context;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ModifyTaskCommand extends Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(context, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(context, filterPropertyArgs);

            List<Task> tasks = context.getTaskUseCase().getTasks(
                    filterPropertySpecs, null, null, taskUUIDs);

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ModifyTaskCommand) obj;
        return Objects.equals(this.tempIDs, that.tempIDs) &&
                Objects.equals(this.filterPropertyArgs, that.filterPropertyArgs) &&
                Objects.equals(this.modifyPropertyArgs, that.modifyPropertyArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tempIDs, filterPropertyArgs, modifyPropertyArgs);
    }

    @Override
    public String toString() {
        return "ModifyTaskCommand[" +
                "tempIDs=" + tempIDs + ", " +
                "filterPropertyArgs=" + filterPropertyArgs + ", " +
                "modifyPropertyArgs=" + modifyPropertyArgs + ']';
    }

    private List<@NonNull Integer> tempIDs;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private List<@NonNull PropertyArgument> modifyPropertyArgs;

}

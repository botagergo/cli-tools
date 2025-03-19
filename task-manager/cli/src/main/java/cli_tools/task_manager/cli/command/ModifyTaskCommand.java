package cli_tools.task_manager.cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.property_modifier.PropertyModifier;
import cli_tools.common.cli.string_to_property_converter.StringToPropertyConverterException;
import cli_tools.task_manager.task.Task;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.data.property.ModifyPropertySpec;
import cli_tools.task_manager.cli.Context;

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

            List<Task> tasks = context.getTaskService().getTasks(
                    filterPropertySpecs, null, null, taskUUIDs);

            tasks = CommandUtil.confirmAndGetTasksToChange(context, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.MODIFY);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }

            for (Task task : tasks) {
                UUID taskUuid = task.getUUID();
                if (modifyPropertyArgs != null) {
                    List<ModifyPropertySpec> modifyPropertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                    PropertyModifier.modifyProperties(context.getPropertyManager(), task, modifyPropertySpecs);
                }
                Task modifiedTask = context.getTaskService().modifyTask(taskUuid, task);
                if (tasks.size() == 1) {
                    int tempID = context.getTempIDMappingService().getOrCreateID(modifiedTask.getUUID());
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

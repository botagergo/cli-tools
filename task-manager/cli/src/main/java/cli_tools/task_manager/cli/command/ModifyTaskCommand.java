package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.property_modifier.PropertyModifier;
import cli_tools.common.cli.string_to_property_converter.StringToPropertyConverterException;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.data.property.ModifyPropertySpec;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.backend.task.Task;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class ModifyTaskCommand extends Command {

    private List<@NonNull Integer> tempIDs;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private List<@NonNull PropertyArgument> modifyPropertyArgs;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        TaskManagerContext taskManagerContext = (TaskManagerContext) context;

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);

            List<Task> tasks = taskManagerContext.getTaskService().getTasks(
                    filterPropertySpecs, null, null, taskUUIDs, true);

            tasks = CommandUtil.confirmAndGetTasksToChange(taskManagerContext, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.MODIFY);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }

            for (Task task : tasks) {
                UUID taskUuid = task.getUUID();
                if (modifyPropertyArgs != null) {
                    List<ModifyPropertySpec> modifyPropertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                    PropertyModifier.modifyProperties(context.getPropertyManager(), task, modifyPropertySpecs);
                }
                Task modifiedTask = taskManagerContext.getTaskService().modifyTask(taskUuid, task);
                if (tasks.size() == 1) {
                    int tempID = context.getTempIdManager().getOrCreateID(modifiedTask.getUUID());
                    context.setPrevTempId(tempID);
                }
            }

        } catch (StringToPropertyConverterException e) {
            switch (e.getExceptionType()) {
                case NotAList ->
                        Print.printError("a list of values was provided, but property '" + e.getArgument() + "' is not a list");
                case EmptyList -> Print.printError("no value was provided for property '" + e.getArgument() + "'");
                case LabelNotFound -> Print.printInfo("no changes were made");
                case OrderedLabelNotFound -> Print.printError("no such label: '" + e.getArgument() + "'");
                default -> Print.printError(e.getMessage());
            }
        } catch (Exception e) {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(tempIDs, filterPropertyArgs, modifyPropertyArgs);
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
    public String toString() {
        return "ModifyTaskCommand[" +
                "tempIDs=" + tempIDs + ", " +
                "filterPropertyArgs=" + filterPropertyArgs + ", " +
                "modifyPropertyArgs=" + modifyPropertyArgs + ']';
    }

}

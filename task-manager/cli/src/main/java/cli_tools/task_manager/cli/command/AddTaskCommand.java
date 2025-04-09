package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.property_modifier.PropertyModifier;
import cli_tools.task_manager.task.Task;
import cli_tools.common.core.data.property.ModifyPropertySpec;

import java.util.List;

@Log4j2
@Getter
@Setter
public final class AddTaskCommand extends Command {

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        try {
            Task task = new Task();

            context.getPropertyManager().setProperty(task, "name", name);

            if (modifyPropertyArgs != null) {
                List<ModifyPropertySpec> propertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                PropertyModifier.modifyProperties(context.getPropertyManager(), task, propertySpecs);
            }

            Task addedTask = ((TaskManagerContext) context).getTaskService().addTask(task);
            int tempID = context.getTempIdManager().getOrCreateID(addedTask.getUUID());
            context.setPrevTempId(tempID);
        } catch (Exception e) {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private String name;
    private List<@NonNull PropertyArgument> modifyPropertyArgs;

}

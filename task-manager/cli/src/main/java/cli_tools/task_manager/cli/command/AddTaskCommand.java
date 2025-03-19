package cli_tools.task_manager.cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.property_modifier.PropertyModifier;
import cli_tools.task_manager.task.Task;
import cli_tools.common.core.data.property.ModifyPropertySpec;
import cli_tools.task_manager.cli.Context;

import java.util.List;

@Log4j2
@Getter
@Setter
public final class AddTaskCommand extends Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            Task task = new Task();

            context.getPropertyManager().setProperty(task, "name", name);

            if (modifyPropertyArgs != null) {
                List<ModifyPropertySpec> propertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                PropertyModifier.modifyProperties(context.getPropertyManager(), task, propertySpecs);
            }

            Task addedTask = context.getTaskService().addTask(task);
            int tempID = context.getTempIDMappingService().getOrCreateID(addedTask.getUUID());
            context.setPrevTaskID(tempID);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private String name;
    private List<@NonNull PropertyArgument> modifyPropertyArgs;

}

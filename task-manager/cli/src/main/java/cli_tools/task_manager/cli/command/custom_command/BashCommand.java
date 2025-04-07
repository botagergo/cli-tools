package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.custom_command.CustomCommand;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.CommandUtil;
import cli_tools.task_manager.task.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
@Log4j2
public class BashCommand extends CustomCommand {

    @Override
    public void execute(Context context) {
        TaskManagerContext taskManagerContext = (TaskManagerContext) context;

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);

            List<Task> tasks = taskManagerContext.getTaskService().getTasks(filterPropertySpecs, null, null, taskUUIDs);

            final ProcessBuilder p = new ProcessBuilder().command("bash", "-c", bashCommand);

            fillEnv(tasks, p.environment(), context);
            p.redirectInput(ProcessBuilder.Redirect.INHERIT);
            p.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            p.redirectError(ProcessBuilder.Redirect.INHERIT);

            p.start().waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private void fillEnv(List<Task> tasks, Map<String, String> env, Context context) throws PropertyException, IOException {
        for (int i = 0; i < tasks.size(); i++) {
            for (var entry : tasks.get(i).getProperties().entrySet()) {
                String propertyName = entry.getKey();
                PropertyDescriptor propertyDescriptor = context.getPropertyManager().getPropertyDescriptor(propertyName);
                String propertyValueStr = context.getPropertyToStringConverter().propertyToString(propertyName, Property.from(propertyDescriptor, entry.getValue()));
                env.put("TASK_" + i + "_" + propertyName.toUpperCase(),  propertyValueStr);
            }
        }
    }

    private List<@NonNull Integer> tempIDs;
    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private final String bashCommand;
    private final int timeoutMillis;

}

package task_manager.ui.cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.core.data.Task;
import task_manager.core.property.PropertySpec;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.ui.cli.command.property_modifier.PropertyModifier;
import task_manager.ui.cli.command.string_to_property_converter.StringToPropertyConverterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public record ModifyTaskCommand(
        @NonNull List<Integer> taskIDs,
        List<PropertyArgument> properties
) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        if (taskIDs.isEmpty()) {
            System.out.println("No selector was specified");
            return;
        }

        try {
            List<Task> tasks = new ArrayList<>();

            for (int taskID : taskIDs) {
                UUID uuid = context.getTempIDMappingRepository().getUUID(taskID);
                if (uuid == null) {
                    System.out.println("Task ID does not exist: " + taskID);
                }

                tasks.add(context.getTaskUseCase().getTask(uuid));
            }

            for (Task task : tasks) {
                if (properties != null) {
                    List<PropertySpec> propertySpecs = context.getStringToPropertyConverter().convertProperties(properties, true);
                    PropertyModifier.modifyProperties(context.getPropertyManager(), task, propertySpecs);
                }
                context.getTaskUseCase().modifyTask(task);
            }

        } catch (StringToPropertyConverterException e) {
            switch (e.getExceptionType()) {
                case NotAList -> System.out.println("A list of values was provided, but property '" + e.getPropertyDescriptor().name() + "' is not a list");
                case EmptyList -> System.out.println("No value was provided for property '" + e.getPropertyDescriptor().name() + "'");
                case LabelNotFound -> System.out.println("No changes were made");
                case OrderedLabelNotFound -> System.out.println("Label not found: " + e.getPropertyValue());
                case InvalidBoolean -> System.out.println("Invalid boolean value: " + e.getPropertyValue());
                case InvalidInteger -> System.out.println("Invalid integer value: " + e.getPropertyValue());
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

}

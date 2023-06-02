package task_manager.ui.cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Triple;
import task_manager.data.Task;
import task_manager.property.PropertySpec;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.command.property_converter.PropertyConverterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public record ModifyTaskCommand(@NonNull List<Integer> taskIDs, List<Triple<PropertySpec.Affinity, String, List<String>>> properties) implements Command {

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
                    List<PropertySpec> propertySpecs = context.getPropertyConverter().convertProperties(properties);
                    context.getPropertyManager().modifyProperties(task, propertySpecs);
                }
                context.getTaskUseCase().modifyTask(task);
            }

        } catch (PropertyConverterException e) {
            switch (e.getExceptionType()) {
                case NotAList -> System.out.println("A list of values was provided, but property '" + e.getPropertyDescriptor().name() + "' is not a list");
                case EmptyList -> System.out.println("No value was provided for property '" + e.getPropertyDescriptor().name() + "'");
                case LabelNotFound -> System.out.println("No changes were made");
                case InvalidBoolean -> System.out.println("Invalid boolean value: " + e.getPropertyValue());
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

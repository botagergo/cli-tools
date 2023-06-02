package task_manager.ui.cli.command;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;
import task_manager.data.Task;
import task_manager.property.PropertySpec;
import task_manager.ui.cli.Context;

@Log4j2
public record AddTaskCommand(String name, List<Triple<PropertySpec.Affinity, String, List<String>>> properties) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            Task task = new Task();

            if (properties != null) {
                List<PropertySpec> propertySpecs = context.getPropertyConverter().convertProperties(properties);
                context.getPropertyManager().modifyProperties(task, propertySpecs);
            }

            context.getPropertyManager().setProperty(task, "name", name);

            context.getTaskUseCase().addTask(task);
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

package cli_tools.task_manager.cli.task_printer;

import cli_tools.common.cli.property_to_string_converter.PropertyToStringConverter;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.task_manager.cli.TaskManagerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class JsonTaskPrinter extends TaskPrinter {

    boolean isPretty;

    private static ObjectMapper objectMapper;

    @Override
    public void printTasks(List<Task> tasks, List<String> properties, TaskManagerContext taskManagerContext) throws TaskPrinterException {
        try {
            if (isPretty) {
                Print.print(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tasks.stream().map(Task::getProperties).toList()));
            } else {
                Print.print(getObjectMapper().writer().writeValueAsString(tasks.stream().map(Task::getProperties).toList()));
            }
        } catch (JsonProcessingException e) {
            throw new TaskPrinterException(e.getMessage(), e);
        }
    }

    @Override
    public void printTasksTrees(List<PropertyOwnerTree> taskTrees, List<String> properties, TaskManagerContext taskManagerContext) throws TaskPrinterException {
        throw new TaskPrinterException("Hierarchical printing is not supported for JSON output");
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

}

package task_manager.task_manager_cli.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.*;

public final class AIResponseAddTasks {
    @JsonPropertyDescription("""
            Contains a list of tasks to create, with the necessary properties.
            The following fields are available:
            - name: name of the task
            """)
    public List<Map<String, Object>> tasksToAdd;
}

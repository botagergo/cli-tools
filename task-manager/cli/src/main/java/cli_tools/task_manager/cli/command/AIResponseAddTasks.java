package cli_tools.task_manager.cli.command;

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

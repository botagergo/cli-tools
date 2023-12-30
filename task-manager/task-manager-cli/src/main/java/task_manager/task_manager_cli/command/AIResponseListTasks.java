package task_manager.task_manager_cli.command;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.UUID;

public final class AIResponseListTasks {
    @JsonPropertyDescription("Contains the queried task UUIDs.")
    public ArrayList<UUID> taskUuids;
}

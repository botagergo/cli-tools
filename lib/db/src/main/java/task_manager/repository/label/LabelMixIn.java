package task_manager.repository.label;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LabelMixIn(@JsonProperty(required = true) String name, @JsonProperty(required = true) UUID uuid) {}

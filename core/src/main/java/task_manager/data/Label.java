package task_manager.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.With;

import java.util.UUID;

@With
public record Label(@JsonProperty(required = true) UUID uuid,
                    @JsonProperty(required = true) String name)
{}

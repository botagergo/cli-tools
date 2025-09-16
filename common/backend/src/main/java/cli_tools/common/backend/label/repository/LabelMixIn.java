package cli_tools.common.backend.label.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LabelMixIn(@JsonProperty(required = true) String name, @JsonProperty(required = true) UUID uuid) {
}

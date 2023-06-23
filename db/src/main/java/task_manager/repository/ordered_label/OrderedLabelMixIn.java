package task_manager.repository.ordered_label;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderedLabelMixIn(@JsonProperty(required = true) String text, @JsonProperty(required = true) int value) {
}

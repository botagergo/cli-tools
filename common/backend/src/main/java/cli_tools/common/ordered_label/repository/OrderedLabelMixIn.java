package cli_tools.common.ordered_label.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderedLabelMixIn(@JsonProperty(required = true) String text, @JsonProperty(required = true) int value) {
}

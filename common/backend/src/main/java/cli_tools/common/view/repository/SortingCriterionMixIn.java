package cli_tools.common.view.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SortingCriterionMixIn(
        @JsonProperty(value = "property", required = true) String propertyName,
        boolean ascending
) {
}
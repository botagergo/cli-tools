package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SortingCriterionMixIn(
        @JsonProperty(value = "property", required = true) String propertyName,
        boolean ascending
) { }
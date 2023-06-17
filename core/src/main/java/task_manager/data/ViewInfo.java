package task_manager.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record ViewInfo(
        @JsonProperty(required = true) @NonNull String name,
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo
) { }

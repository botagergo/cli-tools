package task_manager.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.List;

public record SortingInfo(
        @JsonProperty(required = true) @NonNull List<SortingCriterion> sortingCriteria
) { }

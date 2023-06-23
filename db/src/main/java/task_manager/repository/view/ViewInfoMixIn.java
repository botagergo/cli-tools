package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.SortingInfo;

public record ViewInfoMixIn(
        @JsonProperty(required = true) String name,
        @JsonProperty("sort")
        SortingInfo sortingInfo,
        @JsonProperty("filter")
        @JsonDeserialize(using = FilterCriterionInfoDeserializer.class)
        FilterCriterionInfo filterCriterionInfo
) {}

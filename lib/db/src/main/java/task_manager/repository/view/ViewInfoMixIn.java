package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.OutputFormat;
import task_manager.core.data.SortingInfo;

import java.util.List;

@JsonPropertyOrder({ "name", "sort", "filter" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViewInfoMixIn(
        @JsonProperty(required = true) String name,
        @JsonProperty("sort")
        SortingInfo sortingInfo,
        @JsonProperty("filter")
        @JsonDeserialize(using = FilterCriterionInfoDeserializer.class)
        FilterCriterionInfo filterCriterionInfo,
        @JsonProperty("properties")
        List<String> propertiesToList,
        @JsonProperty("outputFormat")
        OutputFormat outputFormat,
        @JsonProperty(value = "hierarchical", defaultValue = "true")
        boolean hierarchical
) {}

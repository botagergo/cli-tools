package cli_tools.common.backend.view.repository;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonPropertyOrder({"name", "sort", "filter"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViewInfoMixIn(
        @JsonProperty("sort")
        SortingInfo sortingInfo,
        @JsonProperty("filter")
        @JsonDeserialize(using = FilterCriterionInfoDeserializer.class)
        FilterCriterionInfo filterCriterionInfo,
        @JsonProperty("properties")
        List<String> propertiesToList,
        @JsonProperty("outputFormat")
        String outputFormat,
        @JsonProperty(value = "hierarchical", defaultValue = "true")
        boolean hierarchical,
        @JsonProperty(value = "listDone", defaultValue = "false")
        boolean listDone
) {
}

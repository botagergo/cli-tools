package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import task_manager.data.FilterCriterionInfo;

import java.util.List;

public record FilterCriterionInfoMixIn(
        String name,
        @JsonProperty(required = true) FilterCriterionInfo.Type type,
        @JsonProperty("property") String propertyName,
        List<FilterCriterionInfo> children,
        FilterCriterionInfo.Predicate predicate,
        List<Object> operands
) { }

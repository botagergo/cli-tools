package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.Predicate;

import java.util.List;

public record FilterCriterionInfoMixIn(
        String name,
        @JsonProperty(required = true) FilterCriterionInfo.Type type,
        @JsonProperty("property") String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        List<Object> operands
) { }

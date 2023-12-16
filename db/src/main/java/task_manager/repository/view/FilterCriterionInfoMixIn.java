package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.Predicate;

import java.util.List;

@JsonPropertyOrder({ "name", "type", "property", "children", "predicate", "operands" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FilterCriterionInfoMixIn(
        String name,
        @JsonProperty(required = true) FilterCriterionInfo.Type type,
        @JsonProperty("property") String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        List<Object> operands
) { }

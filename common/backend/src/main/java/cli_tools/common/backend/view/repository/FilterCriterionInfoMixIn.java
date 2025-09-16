package cli_tools.common.backend.view.repository;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.Predicate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"name", "type", "property", "children", "predicate", "predicateNegated", "operands"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FilterCriterionInfoMixIn(
        String name,
        @JsonProperty(required = true) FilterCriterionInfo.Type type,
        @JsonProperty("property") String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        Predicate predicateNegated,
        List<Object> operands
) {
}

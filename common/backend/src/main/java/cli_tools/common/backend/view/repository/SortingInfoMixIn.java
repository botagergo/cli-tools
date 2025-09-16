package cli_tools.common.backend.view.repository;

import cli_tools.common.core.data.SortingCriterion;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SortingInfoMixIn(
        @JsonProperty(value = "criteria", required = true) List<SortingCriterion> sortingCriteria
) {
}

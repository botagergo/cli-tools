package cli_tools.common.view.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import cli_tools.common.core.data.SortingCriterion;

import java.util.List;

public record SortingInfoMixIn(
        @JsonProperty(value = "criteria", required = true) List<SortingCriterion> sortingCriteria
) { }

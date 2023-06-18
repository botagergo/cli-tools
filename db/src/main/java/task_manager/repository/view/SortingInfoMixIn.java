package task_manager.repository.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import task_manager.data.SortingCriterion;

import java.util.List;

public record SortingInfoMixIn(
        @JsonProperty(value = "criteria", required = true) List<SortingCriterion> sortingCriteria
) { }

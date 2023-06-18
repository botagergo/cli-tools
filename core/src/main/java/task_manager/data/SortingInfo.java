package task_manager.data;

import lombok.NonNull;

import java.util.List;

public record SortingInfo(
        @NonNull List<SortingCriterion> sortingCriteria
) { }

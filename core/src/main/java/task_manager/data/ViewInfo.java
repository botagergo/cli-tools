package task_manager.data;

import lombok.NonNull;

public record ViewInfo(
        @NonNull String name,
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo
) { }

package task_manager.core.data;

import lombok.NonNull;

public record ViewInfo(
        @NonNull String name,
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo
) { }

package task_manager.core.data;

import lombok.NonNull;

public record SortingCriterion(
        @NonNull String propertyName,
        boolean ascending
) { }
package task_manager.data;

import lombok.NonNull;

public record SortingCriterion(
        @NonNull String propertyName,
        boolean ascending
) { }
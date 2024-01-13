package task_manager.core.data;

import lombok.NonNull;

import java.util.List;

public record ViewInfo(
        @NonNull String name,
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo,
        List<String> propertiesToList,
        OutputFormat outputFormat,
        boolean hierarchical
) { }

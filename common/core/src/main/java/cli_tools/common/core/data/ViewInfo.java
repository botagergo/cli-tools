package cli_tools.common.core.data;

import java.util.List;

public record ViewInfo(
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo,
        List<String> propertiesToList,
        OutputFormat outputFormat,
        boolean hierarchical
) { }

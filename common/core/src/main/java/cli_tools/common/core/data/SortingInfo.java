package cli_tools.common.core.data;

import lombok.NonNull;

import java.util.List;

public record SortingInfo(
        @NonNull List<SortingCriterion> sortingCriteria
) { }

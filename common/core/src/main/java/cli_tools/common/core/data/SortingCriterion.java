package cli_tools.common.core.data;

import lombok.NonNull;

public record SortingCriterion(
        @NonNull String propertyName,
        boolean ascending
) {
}
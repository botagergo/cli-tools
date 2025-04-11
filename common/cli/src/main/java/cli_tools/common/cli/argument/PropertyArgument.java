package cli_tools.common.cli.argument;

import cli_tools.common.core.data.property.Affinity;
import lombok.NonNull;

import java.util.List;

public record PropertyArgument(
        @NonNull Affinity affinity,
        @NonNull String propertyName,
        String predicate,
        List<String> values
) {
}

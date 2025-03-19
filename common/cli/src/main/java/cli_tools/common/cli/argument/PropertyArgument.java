package cli_tools.common.cli.argument;

import lombok.NonNull;
import cli_tools.common.core.data.property.Affinity;

import java.util.List;

public record PropertyArgument(
        @NonNull Affinity affinity,
        @NonNull String propertyName,
        String option,
        List<String> values
) { }

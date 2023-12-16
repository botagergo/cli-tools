package task_manager.cli_lib.argument;

import lombok.NonNull;
import task_manager.core.property.Affinity;

import java.util.List;

public record PropertyArgument(
        @NonNull Affinity affinity,
        @NonNull String propertyName,
        String option,
        List<String> values
) { }

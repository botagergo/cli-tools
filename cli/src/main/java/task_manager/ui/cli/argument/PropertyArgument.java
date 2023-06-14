package task_manager.ui.cli.argument;

import lombok.NonNull;
import task_manager.property.PropertySpec;

import java.util.List;

public record PropertyArgument(
        @NonNull PropertySpec.Affinity affinity,
        @NonNull String propertyName,
        String predicate,
        @NonNull List<String> values
) { }

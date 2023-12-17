package task_manager.cli_lib.argument;

import lombok.NonNull;

import java.util.List;

public record OptionArgument(
        @NonNull String optionName,
        List<String> values
) { }

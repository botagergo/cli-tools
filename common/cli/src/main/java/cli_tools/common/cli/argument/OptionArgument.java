package cli_tools.common.cli.argument;

import lombok.NonNull;

import java.util.List;

public record OptionArgument(
        @NonNull String optionName,
        List<String> values
) { }

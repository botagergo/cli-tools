package cli_tools.common.cli.command.custom_command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CustomCommandDefinition {
    @JsonProperty(required = true) protected final String commandName;
}

package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BashCommandDefinition extends CustomCommandDefinition {

    @JsonCreator
    public BashCommandDefinition(
            @JsonProperty(value = "commandName", required = true) String commandName,
            @JsonProperty(value = "bashCommand", required = true) String bashCommand) {
        super(commandName);
        this.bashCommand = bashCommand;
    }

    private final String bashCommand;

}

package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BashCommandDefinition extends CustomCommandDefinition {

    private final String bashCommand;
    private final int timeoutMillis;
    @JsonCreator
    public BashCommandDefinition(
            @JsonProperty(value = "commandName", required = true) String commandName,
            @JsonProperty(value = "bashCommand", required = true) String bashCommand,
            @JsonProperty(value = "timeoutMillis") int timeoutMillis) {
        super(commandName);
        this.bashCommand = bashCommand;
        this.timeoutMillis = timeoutMillis;
    }

}

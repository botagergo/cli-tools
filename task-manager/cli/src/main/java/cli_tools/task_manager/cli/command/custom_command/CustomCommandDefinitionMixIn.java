package cli_tools.task_manager.cli.command.custom_command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BashCommandDefinition.class, name = "Bash")
})
public class CustomCommandDefinitionMixIn { }

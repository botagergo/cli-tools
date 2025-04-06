package cli_tools.task_manager.cli.command.custom_command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BashCommandDefinition.class, name = "BashCommandDefinition")
})
public class CustomCommandDefinitionMixIn { }

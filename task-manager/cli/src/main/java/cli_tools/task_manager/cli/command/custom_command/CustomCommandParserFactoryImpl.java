package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import cli_tools.common.cli.command.custom_command.CustomCommandParser;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomCommandParserFactoryImpl implements CustomCommandParserFactory {
    @Override
    public CustomCommandParser createParser(@NonNull CustomCommandDefinition customCommandDefinition) {
        if (customCommandDefinition instanceof BashCommandDefinition bashCommandDefinition) {
            return new BashCommandParser(bashCommandDefinition.getBashCommand());
        } else {
            log.error("Unknown custom command definition type: {}", customCommandDefinition.getClass().getName());
            return null;
        }
    }
}

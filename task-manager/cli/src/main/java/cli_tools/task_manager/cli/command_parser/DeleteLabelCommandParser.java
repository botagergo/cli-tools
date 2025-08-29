package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.InvalidOptionException;
import cli_tools.task_manager.cli.command.DeleteLabelCommand;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DeleteLabelCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        DeleteLabelCommand command = new DeleteLabelCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("command 'deleteLabel' does not accept modify property arguments");
        } else if (!argList.getLeadingPositionalArguments().isEmpty()) {
            throw new CommandParserException("command 'deleteLabel' does not accept leading positional arguments");
        }

        String type = null;
        for (OptionArgument optionArg : argList.getOptionArguments()) {
            if (optionArg.optionName().equals("type")) {
                type = parseSingleOptionValue("type", optionArg.values());
            } else {
                throw new InvalidOptionException(optionArg.optionName());
            }
        }

        command.setGetLabelTexts(argList.getTrailingPositionalArguments());

        if (type == null) {
            throw new CommandParserException("missing required argument for command 'deleteLabel': type");
        }
        command.setType(type);

        return command;
    }

}

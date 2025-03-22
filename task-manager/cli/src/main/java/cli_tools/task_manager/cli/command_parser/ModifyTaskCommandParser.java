package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.ParseUtil;
import cli_tools.task_manager.cli.command.ModifyTaskCommand;

public class ModifyTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        ModifyTaskCommand command = new ModifyTaskCommand();

        if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        command.setModifyPropertyArgs(argList.getModifyPropertyArguments());
        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());
        command.setTempIDs(ParseUtil.getTempIds(context, argList.getLeadingNormalArguments()));

        return command;
    }

}

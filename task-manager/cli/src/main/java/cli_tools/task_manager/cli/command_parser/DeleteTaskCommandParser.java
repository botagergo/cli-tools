package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.Command;
import cli_tools.task_manager.cli.command.DeleteTaskCommand;

public class DeleteTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        DeleteTaskCommand command = new DeleteTaskCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        } else if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        command.setTempIDs(ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments()));
        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());

        return command;
    }

}

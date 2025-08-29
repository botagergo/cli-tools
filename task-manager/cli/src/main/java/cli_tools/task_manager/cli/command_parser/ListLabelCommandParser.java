package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.InvalidOptionException;
import cli_tools.task_manager.cli.command.ListLabelCommand;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ListLabelCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        ListLabelCommand command = new ListLabelCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("command 'listLabel' does not accept modify property arguments");
        } else if (!argList.getLeadingPositionalArguments().isEmpty()) {
            throw new CommandParserException("command 'listLabel' does not accept leading positional arguments");
        }

        List<String> types = null;
        for (OptionArgument optionArg : argList.getOptionArguments()) {
            if (optionArg.optionName().equals("type")) {
                types = parseListOptionValue(optionArg.values());
            } else {
                throw new InvalidOptionException(optionArg.optionName());
            }
        }

        command.setLabelTypes(types);

        return command;
    }

}

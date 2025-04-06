package cli_tools.common.cli.command.custom_command.repository;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;

import java.io.IOException;
import java.util.List;

public interface CustomCommandRepository {

    List<CustomCommandDefinition> getAll() throws IOException;

}

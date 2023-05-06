package task_manager;

import org.testng.annotations.*;

import task_manager.api.command.ListTasksCommand;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;

import static org.testng.Assert.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListTasksCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        ListTasksCommand cmd =
                (ListTasksCommand) parser.parse(getArgList(List.of(), new LinkedHashMap<>()));
        assertEquals(cmd.getNameQuery(), null);
        assertEquals(cmd.getQueries(), null);
    }

    @Test
    public void testNormalArg() {
        ListTasksCommand cmd = (ListTasksCommand) parser
                .parse(getArgList(List.of("my", "task"), new LinkedHashMap<>()));
        assertEquals(cmd.getNameQuery(), "my task");
        assertEquals(cmd.getQueries(), null);
    }

    @Test
    public void testQuery() {
        ListTasksCommand cmd = (ListTasksCommand) parser.parse(getArgList(List.of(),
                new LinkedHashMap<>(Map.of('?', List.of(new SpecialArgument('?', "name='my task'"),
                        new SpecialArgument('?', "name='other task'"))))));
        assertEquals(cmd.getNameQuery(), null);
        assertEquals(cmd.getQueries(), List.of("name='my task'", "name='other task'"));
    }

    private ArgumentList getArgList(List<String> normalArgs,
            LinkedHashMap<Character, List<SpecialArgument>> specialArgs) {
        ArgumentList argList = new ArgumentList();
        argList.commandName = "list";
        argList.normalArguments = normalArgs;
        argList.specialArguments = specialArgs;
        return argList;
    }

    ListTasksCommandParser parser = new ListTasksCommandParser();
}

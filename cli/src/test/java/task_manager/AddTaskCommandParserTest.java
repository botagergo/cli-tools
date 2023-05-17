package task_manager;

import org.testng.annotations.Test;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.ui.cli.command_parser.AddTaskCommandParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class AddTaskCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        assertEquals(parse().name, "");
    }
    
    @Test
    public void testOneNormalArg() {
        assertEquals(parse("task").name, "task");
    }
    
    @Test
    public void testMultipleNormalArgs() {
        assertEquals(parse("my", "simple", "task").name,
            "my simple task");
    }
    
    @Test
    public void testMultipleNormalArgsWithWhitespace() {
        assertEquals(parse("my ", "simple", " task").name,
            "my  simple  task");
    }
    
    @Test
    public void testOneTag() {
        AddTaskCommand command = parse("task", "/tag");
        assertEquals(command.name, "task");
        assertEquals(command.tagNames, List.of("tag"));
    }
    
    @Test
    public void testMultipleTags() {
        AddTaskCommand command = parse("task", "/tag1", "/tag2", "/tag3");
        assertEquals(command.name, "task");
        assertEquals(command.tagNames, List.of("tag1", "tag2", "tag3"));
    }
    
    @Test
    public void testMultipleSeparatedTags() {
        AddTaskCommand command = parse("my", "/tag1", "simple", "/tag2", "task", "/tag3");
        assertEquals(command.name, "my simple task");
        assertEquals(command.tagNames, List.of("tag1", "tag2", "tag3"));
    }
    
    private AddTaskCommand parse(String... param) {
        return (AddTaskCommand) parser.parse(getArgList(param));
    }
    
    private ArgumentList getArgList(String... param) {
        List<String> paramList = new ArrayList<>(Arrays.asList(param));
        paramList.add(0, "add");
        return ArgumentList.from(paramList);
    }
    
    final AddTaskCommandParser parser = new AddTaskCommandParser();

}

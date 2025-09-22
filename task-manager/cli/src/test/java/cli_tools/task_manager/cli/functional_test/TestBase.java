package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.cli.executor.Executor;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command_parser.*;
import cli_tools.task_manager.cli.init.Initializer;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class TestBase {

    private static final TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<>() {
    };
    protected static ObjectMapper objectMapper = new ObjectMapper();
    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    protected String stdoutStr;
    protected String[] stdoutLines;
    protected TaskManagerContext context;
    protected MockConfigurationRepository configurationRepository;
    protected UUID[] uuids;
    private Executor executor;

    @BeforeClass
    void setup() throws IOException {
        Injector injector = Guice.createInjector(new TestModule());

        Initializer initializer = injector.getInstance(Initializer.class);
        CommandParserFactory commandParserFactory = injector.getInstance(CommandParserFactory.class);
        context = injector.getInstance(TaskManagerContext.class);
        configurationRepository = (MockConfigurationRepository) context.getConfigurationRepository();
        uuids = ((RoundRobinUUIDGenerator) injector.getInstance(UUIDGenerator.class)).getUuids();

        initializer.initialize();

        commandParserFactory.registerParser("add", AddTaskCommandParser::new);
        commandParserFactory.registerParser("list", ListTasksCommandParser::new);
        commandParserFactory.registerParser("done", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("undone", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("clear", ClearCommandParser::new);
        commandParserFactory.registerParser("delete", DeleteTaskCommandParser::new);
        commandParserFactory.registerParser("modify", ModifyTaskCommandParser::new);
        commandParserFactory.registerParser("addLabel", AddLabelCommandParser::new);
        commandParserFactory.registerParser("listLabel", ListLabelCommandParser::new);
        commandParserFactory.registerParser("deleteLabel", DeleteLabelCommandParser::new);

        List<PropertyDescriptor> propertyDescriptors = context.getPropertyDescriptorService().getPropertyDescriptors();
        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));
        executor = injector.getInstance(Executor.class);

        System.setOut(new PrintStream(stdout));
    }

    @AfterClass
    void teardown() {
        System.setOut(originalOut);
    }

    protected void execute(String... commands) {
        stdout.reset();
        for (String command : commands) {
            executor.execute(command);
        }
        stdoutStr = stdout.toString();
        stdoutLines = stdoutStr.split(System.lineSeparator());
    }

    protected void assertStdoutContains(String... strings) {
        for (String string : strings) {
            assertTrue(stdoutStr.contains(string), stdoutStr);
        }
    }

    protected void assertStdoutNumLines(int lineNum) {
        assertEquals(stdoutLines.length, lineNum);
    }

    protected void assertStdoutLineContains(int lineNum, String... strings) {
        for (String string : strings) {
            assertTrue(stdoutLines[lineNum].contains(string), stdoutStr);
        }
    }

    protected void assertStdoutLinesContain(String... strings) {
        for (int i = 0; i <= stdoutLines.length - strings.length; i++) {
            boolean contains = true;
            for (int j = 0; j < strings.length; j++) {
                if (!stdoutLines[i + j].contains(strings[j])) {
                    contains = false;
                    break;
                }
            }
            if (contains) {
                return;
            }
        }
        fail();
    }

    protected void assertStdoutTaskHeaderContains(String... strings) {
        assertTrue(stdoutLines.length >= 2, stdoutStr);
        for (String string : strings) {
            assertTrue(stdoutLines[1].contains(string), stdoutStr);
        }
    }

    protected void assertStdoutTaskHeaderNotContains(String... strings) {
        assertTrue(stdoutLines.length >= 2, stdoutStr);
        for (String string : strings) {
            assertFalse(stdoutLines[1].contains(string), stdoutStr);
        }
    }

    protected void assertStdoutTaskRowContains(int taskNumber, String... strings) {
        int lineNumber = 3 + (2 * taskNumber);
        assertTrue(lineNumber < stdoutLines.length, stdoutStr);
        for (String string : strings) {
            assertTrue(stdoutLines[3 + (2 * taskNumber)].contains(string), stdoutStr);
        }
    }

    protected void assertStdoutNotContains(String... strings) {
        for (String string : strings) {
            assertFalse(stdoutStr.contains(string), stdoutStr);
        }
    }

    protected void assertStdoutNumberOfTasks(int expectedNumberOfTasks) {
        int numberOfTasks = 0;
        int ind = 3;
        while (ind < stdoutLines.length && stdoutLines[ind].startsWith("|")) {
            numberOfTasks++;
            ind += 2;
        }
        assertEquals(numberOfTasks, expectedNumberOfTasks, stdoutStr);
    }

    protected List<Task> parseTasksFromJsonStdout() throws JsonProcessingException {
        return objectMapper.readValue(stdoutStr, typeRef).stream().map(Task::fromMap).toList();
    }

    protected void assertStdoutIsJson() {
        try {
            assertNotNull(objectMapper.readValue(stdoutStr, typeRef).stream().map(Task::fromMap).toList());
        } catch (JsonProcessingException e) {
            fail("output is not json");
        }

    }

}
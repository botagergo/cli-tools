package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.cli.command_line.Executor;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command_parser.*;
import cli_tools.task_manager.cli.init.Initializer;
import cli_tools.task_manager.task.repository.TaskRepository;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.testng.Assert.*;

public class TestBase {

    private Executor executor;
    protected TaskRepository taskRepository;

    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    protected String stdoutStr;
    protected TaskManagerContext context;
    protected MockConfigurationRepository configurationRepository;

    @BeforeClass
    void setup() throws IOException {
        Injector injector = Guice.createInjector(new TestModule());

        Initializer initializer = injector.getInstance(Initializer.class);
        CommandParserFactory commandParserFactory = injector.getInstance(CommandParserFactory.class);
        context = injector.getInstance(TaskManagerContext.class);
        configurationRepository = (MockConfigurationRepository) context.getConfigurationRepository();

        initializer.initialize();

        commandParserFactory.registerParser("add", AddTaskCommandParser::new);
        commandParserFactory.registerParser("list", ListTasksCommandParser::new);
        commandParserFactory.registerParser("done", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("clear", ClearCommandParser::new);
        commandParserFactory.registerParser("delete", DeleteTaskCommandParser::new);
        commandParserFactory.registerParser("modify", ModifyTaskCommandParser::new);

        List<PropertyDescriptor> propertyDescriptors = context.getPropertyDescriptorService().getPropertyDescriptors();
        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));
        executor = injector.getInstance(Executor.class);
        taskRepository = injector.getInstance(TaskRepository.class);

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
    }

    protected void assertStdoutContains(String... strings) {
        for (String string : strings) {
            assertTrue(stdoutStr.contains(string));
        }
    }

    protected void assertStdoutNotContains(String... strings) {
        for (String string : strings) {
            assertFalse(stdoutStr.contains(string));
        }
    }

    protected void assertStdoutNumberOfTasks(int expectedNumberOfTasks) {
        String[] lines = stdoutStr.split("\r\n");
        int numberOfTasks = 0;
        int ind = 3;
        while (ind < lines.length && lines[ind].startsWith("|")) {
            numberOfTasks++;
            ind += 2;
        }
        assertEquals(expectedNumberOfTasks, numberOfTasks);
    }

}
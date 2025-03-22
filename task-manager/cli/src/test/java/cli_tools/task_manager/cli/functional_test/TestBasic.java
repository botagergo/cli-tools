package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.cli.command_line.Executor;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.init.Initializer;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.repository.TaskRepository;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.testng.Assert.*;

public class TestBasic {

    private Executor executor;
    private TaskRepository taskRepository;

    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private String stdoutStr;

    @BeforeClass
    void setup() throws IOException {
        Injector injector = Guice.createInjector(new TestModule());

        Initializer initializer = injector.getInstance(Initializer.class);
        initializer.initialize();

        TaskManagerContext context = injector.getInstance(TaskManagerContext.class);

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

    @Test
    void test_basic() throws IOException {
        execute("add go to the post office status:NextAction effort:High priority:Low",
                "add buy a new TV status:NextAction dueDate:tomorrow",
                "add read a book status:OnHold startDate:+31weeks",
                "add finish tax return status:Waiting");

        List<Task> tasks = taskRepository.getAll();
        assertEquals(tasks.size(), 4);
        assertEquals(tasks.get(0).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(1).getProperties().get("name"), "buy a new TV");
        assertEquals(tasks.get(2).getProperties().get("name"), "read a book");
        assertEquals(tasks.get(3).getProperties().get("name"), "finish tax return");

        execute("list");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV",
                "read a book",
                "finish tax return");

        execute("1 2 modify status:Waiting", "status:Waiting list");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV",
                "finish tax return");
        assertStdoutNotContains("read a book");

        execute("status:OnHold done", "list");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV",
                "finish tax return");
        assertStdoutNotContains("read a book");

        execute("4 delete", "list");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV");
        assertStdoutNotContains("read a book",
                "finish tax return");

        execute("delete", "list");
        assertStdoutContains("NAME");
        assertStdoutNotContains("go to the post office",
                "buy a new TV",
                "read a book",
                "finish tax return");
    }

    private void execute(String... commands) {
        stdout.reset();
        for (String command : commands) {
            executor.execute(command);
        }
        stdoutStr = stdout.toString();
    }

    private void assertStdoutContains(String... strings) {
        for (String string : strings) {
            assertTrue(stdoutStr.contains(string));
        }
    }

    private void assertStdoutNotContains(String... strings) {
        for (String string : strings) {
            assertFalse(stdoutStr.contains(string));
        }
    }

}
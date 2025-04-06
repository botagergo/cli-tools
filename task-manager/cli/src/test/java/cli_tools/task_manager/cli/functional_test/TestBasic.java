package cli_tools.task_manager.cli.functional_test;

import cli_tools.task_manager.task.Task;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class TestBasic extends TestBase {

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

}
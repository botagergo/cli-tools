package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.task_manager.backend.task.Task;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestTempId extends TestBase {

    @Test
    void test_temp_id() throws IOException, ServiceException {
        execute("~ list");
        assertStdoutContains("no previous temp id exists");

        execute("add go to the post office status:NextAction effort:High priority:Low",
                "add buy a new TV status:NextAction dueDate:tomorrow",
                "add read a book status:OnHold startDate:+31weeks priority:High",
                "add finish tax return status:Waiting");

        execute("222 333 list");
        assertStdoutContains("no task with temporary ID: 222, 333");

        execute("0 list");
        assertStdoutContains("invalid temporary id: 0");

        execute("2 4 delete");
        List<Task> tasks = context.getTaskService().getTasks(false);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(1).getProperties().get("name"), "read a book");

        execute("add register for course",
                "~ 1 modify status:NextAction");
        execute("name:'register for course' list");
        assertStdoutContains("NextAction");

        execute("2 done");
        execute("name:'register for course' list");
        assertStdoutNotContains("NextAction");

        execute("list");

        execute("1 3 priority.greater:Low list");
        assertStdoutNumberOfTasks(1);
        assertStdoutContains("1 task(s) selected by temporary ID are filtered");
    }

}
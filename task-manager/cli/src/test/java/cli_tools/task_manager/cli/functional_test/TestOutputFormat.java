package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.ViewInfo;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestOutputFormat extends TestBase {

    @BeforeClass
    void setup1() throws ServiceException, IOException {
        context.getViewInfoService().addViewInfo("test_output_format", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion("name", true))))
                .outputFormat("json")
                .build());

        configurationRepository.defaultView = "test_output_format";
        execute("add go to the post office status:NextAction effort:High priority:Low",
                "add buy a new TV status:NextAction dueDate:tomorrow",
                "add read a book status:OnHold startDate:+31weeks",
                "add finish tax return status:Waiting");
    }

    @Test
    void test_outputFormat_text() {
        execute("list /outputFormat:grid");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV",
                "read a book",
                "finish tax return");
    }

    @Test
    void test_outputFormat_json() throws IOException {
        execute("list /outputFormat:json");
        assertJsonOutput();
    }

    @Test
    void test_outputFormat_prettyJson() throws IOException {
        execute("list /outputFormat:prettyJson");
        assertPrettyJsonOutput();
    }

    @Test
    void test_outputFormat_fromViewInfo() throws IOException {
        execute("list");
        assertJsonOutput();
    }

    private void assertJsonOutput() throws JsonProcessingException {
        assertStdoutContains("\"name\":\"buy a new TV\"");

        List<Task> tasks = parseTasksFromJsonStdout();

        assertEquals(tasks.size(), 4);
        assertEquals(tasks.get(0).getProperties().get("name"), "buy a new TV");
        assertEquals(tasks.get(1).getProperties().get("name"), "finish tax return");
        assertEquals(tasks.get(2).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(3).getProperties().get("name"), "read a book");
    }

    private void assertPrettyJsonOutput() throws JsonProcessingException {
        assertStdoutNotContains("\"name\":\"buy a new TV\"");

        List<Task> tasks = parseTasksFromJsonStdout();

        assertEquals(tasks.size(), 4);
        assertEquals(tasks.get(0).getProperties().get("name"), "buy a new TV");
        assertEquals(tasks.get(1).getProperties().get("name"), "finish tax return");
        assertEquals(tasks.get(2).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(3).getProperties().get("name"), "read a book");
    }

}
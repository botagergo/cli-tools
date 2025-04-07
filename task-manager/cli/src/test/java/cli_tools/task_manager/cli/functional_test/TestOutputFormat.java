package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.ViewInfo;
import cli_tools.task_manager.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

public class TestOutputFormat extends TestBase{

    @BeforeClass
    void setup1() throws IOException {
        context.getViewInfoService().addViewInfo("test_output_format", new ViewInfo(
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                null, null, OutputFormat.JSON, false));
        configurationRepository.defaultView = "test_output_format";
        execute("add go to the post office status:NextAction effort:High priority:Low",
                "add buy a new TV status:NextAction dueDate:tomorrow",
                "add read a book status:OnHold startDate:+31weeks",
                "add finish tax return status:Waiting");
    }

    @Test
    void test_outputFormat_text() {
        execute("list .outputFormat:text");
        assertStdoutContains("NAME",
                "go to the post office",
                "buy a new TV",
                "read a book",
                "finish tax return");
    }

    @Test
    void test_outputFormat_json() throws IOException {
        execute("list .outputFormat:json");
        assertJsonOutput();
    }

    @Test
    void test_outputFormat_prettyJson() throws IOException {
        execute("list .outputFormat:prettyJson");
        assertPrettyJsonOutput();
    }

    @Test
    void test_outputFormat_fromViewInfo() throws IOException {
        execute("list");
        assertJsonOutput();
    }

    private void assertJsonOutput() throws JsonProcessingException {
        assertTrue(stdoutStr.contains("\"name\":\"buy a new TV\""));

        TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<>() {};
        List<Task> tasks = objectMapper.readValue(stdoutStr, typeRef).stream().map(Task::fromMap).toList();

        assertEquals(tasks.size(), 4);
        assertEquals(tasks.get(0).getProperties().get("name"), "buy a new TV");
        assertEquals(tasks.get(1).getProperties().get("name"), "finish tax return");
        assertEquals(tasks.get(2).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(3).getProperties().get("name"), "read a book");
    }

    private void assertPrettyJsonOutput() throws JsonProcessingException {
        assertFalse(stdoutStr.contains("\"name\":\"buy a new TV\""));

        TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<>() {};
        List<Task> tasks = objectMapper.readValue(stdoutStr, typeRef).stream().map(Task::fromMap).toList();

        assertEquals(tasks.size(), 4);
        assertEquals(tasks.get(0).getProperties().get("name"), "buy a new TV");
        assertEquals(tasks.get(1).getProperties().get("name"), "finish tax return");
        assertEquals(tasks.get(2).getProperties().get("name"), "go to the post office");
        assertEquals(tasks.get(3).getProperties().get("name"), "read a book");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

}
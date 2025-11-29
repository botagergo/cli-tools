package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.backend.configuration.ConfigurationRepositoryImpl;
import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestListProperties extends TestBase {

    @BeforeClass
    void setup() throws ServiceException, IOException {
        super.setup();
        context.getViewInfoService().addViewInfo("view", ViewInfo.builder()
                .propertiesToList(List.of("id", "name", "priority"))
                .build());
        configurationRepository.defaultView = null;
    }

    @Test
    void test_listProperties() throws IOException {
        execute("list");
        assertStdoutTaskHeader("ID", "NAME", "STATUS", "TAGS");

        execute("list $uuid,name,effort");
        assertStdoutTaskHeader("UUID", "NAME", "EFFORT");

        execute("list $+effort,priority");
        assertStdoutTaskHeader("ID", "NAME", "STATUS", "TAGS", "EFFORT", "PRIORITY");

        execute("list view $id,name");
        assertStdoutTaskHeader("ID", "NAME");

        execute("list view $+effort");
        assertStdoutTaskHeader("ID", "NAME", "PRIORITY", "EFFORT");

        execute("list view /properties:name,startDate,dueDate");
        assertStdoutTaskHeader("NAME", "STARTDATE", "DUEDATE");

        execute("list view $id,name,dueDate /properties:name");
        assertStdoutTaskHeader("ID", "NAME", "DUEDATE");

        execute("list $+dueDate /properties:name");
        assertStdoutTaskHeader("NAME", "DUEDATE");

        execute("list /properties:name,effort");
        assertStdoutTaskHeader("NAME", "EFFORT");
    }

}
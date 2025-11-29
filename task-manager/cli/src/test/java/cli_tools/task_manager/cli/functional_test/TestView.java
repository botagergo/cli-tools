package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestView extends TestBase {

    @BeforeClass
    void setup() throws ServiceException, IOException {
        super.setup();
        execute("add go to the post office status:NextAction effort:Medium priority:Low",
                "add buy a new TV status:NextAction effort:Low dueDate:tomorrow",
                "add read a book status:OnHold effort:Low startDate:+31weeks priority:High parent:%s".formatted(uuids[7]),
                "add buy a book status:Waiting parent",
                "add repair bicycle effort:High");

        context.getViewInfoService().addViewInfo("default_view", ViewInfo.builder()
                .filterCriterionInfo(FilterCriterionInfo.builder()
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of("read a book"))
                        .build())
                .outputFormat("json")
                .build());

        context.getViewInfoService().addViewInfo("view1", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(
                        new SortingCriterion("priority", false),
                        new SortingCriterion("name", true))))
                .filterCriterionInfo(FilterCriterionInfo.builder()
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("effort")
                        .predicate(Predicate.LESS)
                        .operands(List.of(3)) // Medium
                        .build())
                .propertiesToList(List.of("id", "name", "priority"))
                .outputFormat("grid")
                .build());

        context.getViewInfoService().addViewInfo("view2", ViewInfo.builder()
                .propertiesToList(List.of("id", "name", "priority"))
                .outputFormat("json")
                .build());

        context.getViewInfoService().addViewInfo("view3", ViewInfo.builder()
                .propertiesToList(List.of("id", "name", "priority"))
                .outputFormat("prettyJson")
                .build());

        context.getViewInfoService().addViewInfo("view4", ViewInfo.builder()
                .propertiesToList(List.of("id", "name", "priority"))
                .outputFormat("grid")
                .hierarchical(true)
                .build());

        context.getViewInfoService().addViewInfo("view5", ViewInfo.builder().build());

        context.getViewInfoService().addViewInfo("view6", ViewInfo.builder()
                .outputFormat("json")
                .hierarchical(true)
                .build());

        context.getViewInfoService().addViewInfo("view7", ViewInfo.builder()
                .outputFormat("prettyJson")
                .hierarchical(true)
                .build());

        configurationRepository.defaultView = "default_view";
    }

    @Test
    void test_view() throws IOException {
        execute("list /view:view123");
        assertStdoutContains("no such view");

        execute("list /view");
        assertStdoutContains("option 'view' needs an argument");

        execute("list /view:");
        assertStdoutContains("option 'view' needs an argument");

        execute("list /view:view1,view2");
        assertStdoutContains("option 'view' accepts one argument");

        execute("list");
        assertEquals(parseTasksFromJsonStdout().size(), 1);

        execute("list /view:view1");
        assertStdoutTaskHeaderContains("ID", "NAME", "PRIORITY");
        assertStdoutTaskHeaderNotContains("EFFORT", "STATUS", "TAGS");
        assertStdoutNotContains("•");
        assertStdoutNumberOfTasks(4);
        assertStdoutTaskRowContains(0, "read a book");
        assertStdoutTaskRowContains(1, "go to the post office");
        assertStdoutTaskRowContains(2, "buy a book");
        assertStdoutTaskRowContains(3, "buy a new TV");

        execute("list view1");
        assertStdoutTaskHeaderContains("ID", "NAME", "PRIORITY");
        assertStdoutTaskHeaderNotContains("EFFORT", "STATUS", "TAGS");
        assertStdoutNotContains("•");
        assertStdoutNumberOfTasks(4);
        assertStdoutTaskRowContains(0, "read a book");
        assertStdoutTaskRowContains(1, "go to the post office");
        assertStdoutTaskRowContains(2, "buy a book");
        assertStdoutTaskRowContains(3, "buy a new TV");

        execute("list view2");
        assertStdoutContains("\"name\":\"buy a new TV\"");
        assertStdoutIsJson();

        execute("list view3");
        assertStdoutNotContains("\"name\":\"buy a new TV\"");
        assertStdoutIsJson();

        execute("list view4");
        assertStdoutContains("•");

        execute("list view5");
        assertStdoutContains("go to the post office");

        execute("list view6");
        assertStdoutContains("hierarchical printing is not supported");

        execute("list view7");
        assertStdoutContains("hierarchical printing is not supported");
    }

}
package cli_tools.task_manager.cli.functional_test;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestLabel extends TestBase {

    @Test
    void test_label() throws IOException {
        execute("addLabel");
        assertStdoutContains("missing");

        execute("addLabel .type:label_type label1 label2");
        assertStdoutNumLines(2);
        assertStdoutLineContains(0, "Created label");
        assertStdoutLineContains(1, "Created label");

        execute("addLabel .type:label_type label3");
        assertStdoutNumLines(1);
        assertStdoutLineContains(0, "Created label");

        execute("addLabel .type:label_type1 label4");
        assertStdoutNumLines(1);
        assertStdoutLineContains(0, "Created label");

        var labels = context.getLabelService().getAllLabels();
        assertEquals(labels.get("label_type"), List.of("label1", "label2", "label3"));
        assertEquals(labels.get("label_type1"), List.of("label4"));

        execute("listLabel");
        assertStdoutLinesContain("label_type", "---", "label1", "label2", "label3");
        assertStdoutLinesContain("label_type1", "---", "label4");

        execute("listLabel .type:label_type");
        assertStdoutLinesContain("label1", "label2", "label3");
        assertStdoutNotContains("label_type1");

        execute("deleteLabel .type:label_type2 label1 label2");
        assertStdoutNumLines(2);
        assertStdoutLineContains(0, "does not exist");
        assertStdoutLineContains(1, "does not exist");

        execute("deleteLabel .type:label_type label1 label123");
        assertStdoutNumLines(2);
        assertStdoutLineContains(0, "Deleted label");
        assertStdoutLineContains(1, "does not exist");

        execute("deleteLabel .type:label_type1 label4");
        assertStdoutNumLines(1);
        assertStdoutLineContains(0, "Deleted label");

        labels = context.getLabelService().getAllLabels();
        assertEquals(labels.get("label_type"), List.of("label2", "label3"));
        assertEquals(labels.get("label_type1"), List.of());
    }

}

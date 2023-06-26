package task_manager.repository.view;

import org.testng.annotations.Test;
import task_manager.core.data.*;
import task_manager.repository.util.JsonRepositoryCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.*;

public class JsonViewInfoRepositoryTest {

    public JsonViewInfoRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_get_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
        {
            "view1": {
                "name": "view1",
                "sort": {
                    "criteria": [
                        {"property": "name", "ascending": true},
                        {"property": "done", "ascending": false}
                    ]
                },
                "filter": {
                    "type": "AND",
                    "children": [
                        {"type": "PROPERTY", "property":"done", "predicate": "EQUALS", "operands":[false]},
                        {"type": "PROPERTY", "property":"name", "predicate": "CONTAINS", "operands":["str"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "LESS", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "LESS_EQUAL", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "GREATER", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "GREATER_EQUAL", "operands":["medium"]}
                    ]
                }
            },
            "view2": {
                "name": "view2",
                "filter": {
                    "name": "filter1",
                    "type": "OR",
                    "children": [
                        {"type": "PROPERTY", "property":"done", "predicate": "EQUALS", "operands":[false]},
                        {"type": "NOT", "children": [
                            {"type": "PROPERTY", "property":"name", "predicate": "CONTAINS", "operands":["str"]}
                            ]
                        }
                    ]
                }
            },
            "view3": {
                "name": "view3",
                "sort": {
                    "criteria": [
                        {"property": "name", "ascending": true}
                    ]
                }
            },
            "view4": {
                "name": "view4"
            }
        }
        """);
        repository = new JsonViewInfoRepository(tempFile);
        assertEquals(repository.get("view1"),
                new ViewInfo(
                        "view1",
                        new SortingInfo(
                                List.of(
                                        new SortingCriterion("name", true),
                                        new SortingCriterion("done", false)
                                )
                        ),
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.AND, null,
                                List.of(
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "done", null, Predicate.EQUALS, List.of(false)),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "name", null, Predicate.CONTAINS, List.of("str")),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.LESS, List.of("medium")),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.LESS_EQUAL, List.of("medium")),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.GREATER, List.of("medium")),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.GREATER_EQUAL, List.of("medium"))
                                        ),
                                null, null)));
        assertEquals(repository.get("view2"),
                new ViewInfo(
                        "view2",
                        null,
                        new FilterCriterionInfo(
                                "filter1",
                                FilterCriterionInfo.Type.OR,
                                null,
                                List.of(
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "done", null, Predicate.EQUALS, List.of(false)),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.NOT, null,
                                                List.of(
                                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "name", null, Predicate.CONTAINS, List.of("str"))
                                                ),
                                                null, null)),
                                null, null)));
        assertEquals(repository.get("view3"),
                new ViewInfo(
                        "view3",
                        new SortingInfo(
                                List.of(new SortingCriterion("name", true))
                        ),
                        null
                ));
        assertEquals(repository.get("view4"), new ViewInfo("view4", null, null));
    }

    @Test
    public void test_get_empty() throws IOException {
        File tempFile = rc.makeTempFile("test_get_empty", "{}");
        repository = new JsonViewInfoRepository(tempFile);
        assertNull(repository.get("view1"));
    }

    @Test
    public void test_get_notExist() throws IOException {
        File tempFile = rc.getTempFile("test_get_notExist");
        repository = new JsonViewInfoRepository(tempFile);
        assertNull(repository.get("view1"));
    }

    @Test
    public void test_create() throws IOException {
        File tempFile = rc.getTempFile("test_create");
        repository = new JsonViewInfoRepository(tempFile);
        repository.create(new ViewInfo(
                "view1",
                new SortingInfo(
                        List.of(
                                new SortingCriterion("name", true),
                                new SortingCriterion("done", false)
                        )
                ),
                new FilterCriterionInfo(
                        null, FilterCriterionInfo.Type.AND, null,
                        List.of(
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "done", null, Predicate.EQUALS, List.of(false)),
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "name", null, Predicate.CONTAINS, List.of("str")),
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.LESS, List.of("medium")),
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.LESS_EQUAL, List.of("medium")),
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.GREATER, List.of("medium")),
                                new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "priority", null, Predicate.GREATER_EQUAL, List.of("medium"))
                        ),
                        null, null)));
        repository.create(new ViewInfo(
                        "view2",
                        null,
                        new FilterCriterionInfo(
                                "filter1",
                                FilterCriterionInfo.Type.OR,
                                null,
                                List.of(
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "done", null, Predicate.EQUALS, List.of(false)),
                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.NOT, null,
                                                List.of(
                                                        new FilterCriterionInfo(null, FilterCriterionInfo.Type.PROPERTY, "name", null, Predicate.CONTAINS, List.of("str"))
                                                ),
                                                null, null)),
                                null, null))
        );
        repository.create(new ViewInfo(
                "view3",
                new SortingInfo(
                        List.of(new SortingCriterion("name", true))
                ),
                null
        ));
        repository.create(new ViewInfo("view4", null, null));

        assertEquals(Files.readString(tempFile.toPath()), """
        {
            "view1": {
                "name": "view1",
                "sort": {
                    "criteria": [
                        {"property": "name", "ascending": true},
                        {"property": "done", "ascending": false}
                    ]
                },
                "filter": {
                    "type": "AND",
                    "children": [
                        {"type": "PROPERTY", "property":"done", "predicate": "EQUALS", "operands":[false]},
                        {"type": "PROPERTY", "property":"name", "predicate": "CONTAINS", "operands":["str"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "LESS", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "LESS_EQUAL", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "GREATER", "operands":["medium"]},
                        {"type": "PROPERTY", "property":"priority", "predicate": "GREATER_EQUAL", "operands":["medium"]}
                    ]
                }
            },
            "view2": {
                "name": "view2",
                "filter": {
                    "name": "filter1",
                    "type": "OR",
                    "children": [
                        {"type": "PROPERTY", "property":"done", "predicate": "EQUALS", "operands":[false]},
                        {"type": "NOT", "children": [
                            {"type": "PROPERTY", "property":"name", "predicate": "CONTAINS", "operands":["str"]}
                            ]
                        }
                    ]
                }
            },
            "view3": {
                "name": "view3",
                "sort": {
                    "criteria": [
                        {"property": "name", "ascending": true}
                    ]
                }
            },
            "view4": {
                "name": "view4"
            }
        }
        """.replaceAll("\\s", ""));
    }

    @Test
    public void test_get_invalidFormat_throws() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_invalidFormat_throws", """
            [{"view1":{}]
        """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view1"));
    }

    private JsonViewInfoRepository repository;
    private final JsonRepositoryCreator rc;

}

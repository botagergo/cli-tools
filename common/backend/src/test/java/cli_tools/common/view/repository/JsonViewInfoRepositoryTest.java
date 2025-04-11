package cli_tools.common.view.repository;

import cli_tools.common.core.data.*;
import cli_tools.common.service.JsonRepositoryCreator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.*;

public class JsonViewInfoRepositoryTest {

    private final JsonRepositoryCreator rc;
    private JsonViewInfoRepository repository;

    public JsonViewInfoRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    void test_get_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view1": {
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
                        },
                        "hierarchical": true,
                        "listDone": true
                    },
                    "view2": {
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
                        "sort": {
                            "criteria": [
                                {"property": "name", "ascending": true}
                            ]
                        }
                    },
                    "view4": {
                    },
                    "view5": {
                        "filter": {
                            "name": null,
                            "type": "PROPERTY",
                            "property":"name",
                            "predicateNegated": "EQUALS",
                            "operands":["name1", "name2"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);

        assertEquals(repository.get("view1"), ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(
                        new SortingCriterion("name", true),
                        new SortingCriterion("done", false))))
                .filterCriterionInfo(FilterCriterionInfo.builder()
                        .type(FilterCriterionInfo.Type.AND)
                        .children(List.of(
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("done").predicate(Predicate.EQUALS).operands(List.of(false)).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("name").predicate(Predicate.CONTAINS).operands(List.of("str")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.LESS).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.LESS_EQUAL).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.GREATER).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.GREATER_EQUAL).operands(List.of("medium")).build()
                        )).build())
                .hierarchical(true)
                .listDone(true)
                .build());
        assertEquals(repository.get("view2"),
                ViewInfo.builder()
                        .filterCriterionInfo(FilterCriterionInfo.builder()
                                .name("filter1")
                                .type(FilterCriterionInfo.Type.OR)
                                .children(List.of(
                                        FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("done").predicate(Predicate.EQUALS).operands(List.of(false)).build(),
                                        FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.NOT).children(List.of(
                                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("name").predicate(Predicate.CONTAINS).operands(List.of("str")).build()
                                        )).build())).build()).build());
        assertEquals(repository.get("view3"), ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(
                        new SortingCriterion("name", true)))).build());
        assertEquals(repository.get("view4"), ViewInfo.builder().build());
        assertEquals(repository.get("view5"),
                ViewInfo.builder()
                        .filterCriterionInfo(FilterCriterionInfo.builder()
                                .type(FilterCriterionInfo.Type.PROPERTY)
                                .propertyName("name")
                                .predicateNegated(Predicate.EQUALS)
                                .operands(List.of("name1", "name2")).build()).build());
    }

    @Test
    void test_get_empty() throws IOException {
        File tempFile = rc.makeTempFile("test_get_empty", "{}");
        repository = new JsonViewInfoRepository(tempFile);
        assertNull(repository.get("view1"));
    }

    @Test
    void test_get_notExist() throws IOException {
        File tempFile = rc.getTempFile("test_get_notExist");
        repository = new JsonViewInfoRepository(tempFile);
        assertNull(repository.get("view1"));
    }

    @Test
    void test_get_filterTypeMissing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "property": "name",
                            "predicate": "EQUALS",
                            "operands": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterPropertyMissing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROPERTY",
                            "predicate": "EQUALS",
                            "operands": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterPredicateMissing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROPERTY",
                            "property": "tags",
                            "operands": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterPredicateAndPredicateNegated() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROPERTY",
                            "property": "tags",
                            "predicate": "EQUALS",
                            "predicateNegated": "EQUALS",
                            "operands": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterIllegalField() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROPERTY",
                            "property": "tags",
                            "predicate": "EQUALS",
                            "operand": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterIllegalType() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROP",
                            "property": "tags",
                            "predicate": "EQUALS",
                            "operand": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_filterIllegalPredicate() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROP",
                            "property": "tags",
                            "predicate": "PREDICATE",
                            "operand": ["name1"]
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view"));
    }

    @Test
    void test_get_operandsMissing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                {
                    "view": {
                        "filter": {
                            "type": "PROPERTY",
                            "property": "tags",
                            "predicate": "EMPTY"
                        }
                    }
                }
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertEquals(repository.get("view"),
                ViewInfo.builder()
                        .filterCriterionInfo(FilterCriterionInfo.builder()
                                .type(FilterCriterionInfo.Type.PROPERTY)
                                .propertyName("tags")
                                .predicate(Predicate.EMPTY).build()).build());
    }

    @Test
    void test_create() throws IOException {
        File tempFile = rc.getTempFile("test_create");
        repository = new JsonViewInfoRepository(tempFile);
        repository.create("view1", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(
                        new SortingCriterion("name", true),
                        new SortingCriterion("done", false))))
                .filterCriterionInfo(FilterCriterionInfo.builder()
                        .type(FilterCriterionInfo.Type.AND)
                        .children(List.of(
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("done").predicate(Predicate.EQUALS).operands(List.of(false)).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("name").predicate(Predicate.CONTAINS).operands(List.of("str")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.LESS).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.LESS_EQUAL).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.GREATER).operands(List.of("medium")).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("priority").predicate(Predicate.GREATER_EQUAL).operands(List.of("medium")).build()
                        )).build()).build());

        repository.create("view2", ViewInfo.builder()
                .filterCriterionInfo(FilterCriterionInfo.builder()
                        .name("filter1")
                        .type(FilterCriterionInfo.Type.OR)
                        .children(List.of(
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("done").predicate(Predicate.EQUALS).operands(List.of(false)).build(),
                                FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.NOT).children(List.of(
                                        FilterCriterionInfo.builder().type(FilterCriterionInfo.Type.PROPERTY).propertyName("name").predicate(Predicate.CONTAINS).operands(List.of("str")).build()
                                )).build())).build()).build());

        repository.create("view3", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion("name", true))))
                .build());

        repository.create("view4", ViewInfo.builder().build());

        assertEquals(Files.readString(tempFile.toPath()).replaceAll("\\s", ""), """
                {
                    "view1": {
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
                        },
                        "hierarchical": false,
                        "listDone": false
                    },
                    "view2": {
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
                        },
                        "hierarchical": false,
                        "listDone": false
                    },
                    "view3": {
                        "sort": {
                            "criteria": [
                                {"property": "name", "ascending": true}
                            ]
                        },
                        "hierarchical": false,
                        "listDone": false
                    },
                    "view4": {
                        "hierarchical": false,
                        "listDone": false
                    }
                }
                """.replaceAll("\\s", ""));
    }

    @Test
    void test_get_invalidFormat_throws() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_invalidFormat_throws", """
                    [{"view1":{}]
                """);
        repository = new JsonViewInfoRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("view1"));
    }

}

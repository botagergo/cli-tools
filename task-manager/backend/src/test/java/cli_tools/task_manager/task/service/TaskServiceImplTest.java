package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_converter.PropertyConverterException;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.PropertyOwnerTree;
import common.task.repository.SimpleTaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class TaskServiceImplTest {

    @BeforeClass
    public void init() {
        MockitoAnnotations.openMocks(this);

        simpleTaskRepository = new SimpleTaskRepository();
        propertyManager = new PropertyManager();
        taskService = new TaskServiceImpl(
                simpleTaskRepository,
                propertyManager,
                null,
                propertyConverter,
                tempIDMappingService
        );

        initPropertyDescriptors();
    }

    @BeforeMethod
    public void beforeMethod() {
        taskService.setUuidGenerator(new RoundRobinUUIDGenerator(10));
        initTasks();
    }

    @Test
    public void test_getTasks_all_empty() throws IOException {
        simpleTaskRepository.deleteAll();
        assertEquals(taskService.getTasks().size(), 0);
    }

    @Test
    public void test_getTasks_all() throws IOException {
        List<Task> tasks = taskService.getTasks();

        assertEquals(tasks.size(), 3);
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap(
                "name", "test task", "assignees", Set.of("assignee1", "assignee2"),
                "uuid", uuid1, "tags", List.of("tag1", "tag3"))));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap(
                "name", "other task",
                "uuid", uuid2, "done", true,
                "tags", List.of("tag1", "tag2", "tag3"))));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap(
                "name", "yet another task", "uuid", uuid3,
                "tags", List.of("tag1", "tag2"))));
    }

    @Test
    public void test_getTasks_uuids_empty() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null, null, List.of());
        assertEquals(tasks.size(), 0);
    }

    @Test
    public void test_getTasks_uuids_notExist() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid1, uuid4)
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
    }

    @Test
    public void test_getTasks_uuids_duplicate() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid3, uuid1, uuid1, uuid1, uuid3)
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
    }

    @Test
    public void test_getTasks_uuids() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid3, uuid1)
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_contains() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.CONTAINS, List.of("other")
                ),
                null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "uuid", null,
                        Predicate.CONTAINS, List.of(uuid2.toString())
                ), null
        ));

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.CONTAINS, List.of()
                ), null
        ));

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.CONTAINS, List.of("str1", "str2")
                ), null
        ));

        tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "tags", null,
                        Predicate.CONTAINS, List.of("tag1", "tag2")
                ), null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_equals() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.EQUALS, List.of("other task")
                ), null
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");

        tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "tags", null,
                        Predicate.EQUALS, List.of("tag1", "tag3")
                ), null
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");

        tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "assignees", null,
                        Predicate.EQUALS, List.of("assignee1", "assignee2")
                ), null
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.EQUALS, List.of("task2", "task1")
                ), null
        ));

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.EQUALS, List.of()
                ), null
        ));
    }

    @Test
    public void test_getTasks_filterCriterionInfo_compare_invalid() {
        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "uuid", null,
                        Predicate.LESS, List.of(uuid2.toString())
                ), null
        ));

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.LESS, List.of()
                ), null
        ));

        assertThrows(TaskServiceException.class, () -> taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.LESS, List.of("str1", "str2")
                ), null
        ));
    }

    @Test
    public void test_getTasks_filterCriterionInfo_less() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.LESS, List.of("test task")
                ), null
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_lessEqual() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.LESS_EQUAL, List.of("test task")
                ), null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_greater() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.GREATER, List.of("test task")
                ), null
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_greaterEqual() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.GREATER_EQUAL, List.of("test task")
                ), null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_in() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.IN, List.of("test task", "other task")
                ),
                null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_null() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "assignees", null,
                        Predicate.NULL, List.of()
                ),
                null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_empty() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "assignees", null,
                        Predicate.EMPTY, List.of()
                ),
                null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_and() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo("test", FilterCriterionInfo.Type.AND, null, List.of(
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.PROPERTY,
                                "name", null,
                                Predicate.IN, List.of("test task", "other task")
                        ),
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.PROPERTY,
                                "tags", null,
                                Predicate.CONTAINS, List.of("tag2")
                        )
                ), null, null), null);
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");

        tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo("test", FilterCriterionInfo.Type.OR, null, List.of(
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.PROPERTY,
                                "name", null,
                                Predicate.EQUALS, List.of("test task")
                        ),
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.PROPERTY,
                                "tags", null,
                                Predicate.CONTAINS, List.of("tag3")
                        )
                ), null, null), null);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    public void test_getTasks_filterCriterionInfo_not() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                new FilterCriterionInfo("test", FilterCriterionInfo.Type.NOT, null, List.of(
                        new FilterCriterionInfo(
                                null, FilterCriterionInfo.Type.PROPERTY,
                                "name", null,
                                Predicate.EQUALS, List.of("yet another task")
                        )), null, null), null);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    public void test_getTasks_filterPropertySpecs() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                List.of(
                        new FilterPropertySpec(propertyManager.getPropertyDescriptorCollection().get("name"), List.of("other"), false, Predicate.CONTAINS)
                ), null, null, null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_sortingInfo() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        List<Task> tasks = taskService.getTasks(
                null,
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                null, null
        );
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
        assertEquals(tasks.get(2).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_getTasks_complex() throws IOException, PropertyException, PropertyConverterException, TaskServiceException {
        simpleTaskRepository.deleteAll();
        simpleTaskRepository.getTasks().addAll(List.of(
                Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)),
                Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)),
                Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3, "done", true)),
                Task.fromMap(Utils.newHashMap("name", "something else", "uuid", uuid3)),
                Task.fromMap(Utils.newHashMap("name", "whatever task", "uuid", uuid3, "done", true))
        ));
        List<Task> tasks = taskService.getTasks(
                List.of(
                        new FilterPropertySpec(propertyManager.getPropertyDescriptorCollection().get("done"), List.of(true), false, Predicate.EQUALS)
                ),
                new SortingInfo(List.of(new SortingCriterion("name", false))),
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.CONTAINS, List.of("task")
                ),
                List.of(uuid1, uuid3, uuid4, uuid2)
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3, "done", true)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
    }

    @Test
    public void test_addTask() throws IOException {
        simpleTaskRepository.deleteAll();
        taskService.addTask(Task.fromMap(Utils.newHashMap("name", "test task")));
        taskService.addTask(Task.fromMap(Utils.newHashMap("name", "other task", "done", true)));
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(
                        Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)),
                        Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true))
                )
        );
    }

    @Test
    public void test_addTask_useDefault() throws IOException {
        simpleTaskRepository.deleteAll();
        propertyManager.setPropertyDescriptorCollection(new PropertyDescriptorCollection());

        addPropertyDescriptor("string_with_default", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_str");
        addPropertyDescriptor("list_with_default", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of("str1", "str2"));
        addPropertyDescriptor("set_with_default", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Set.of("str1", "str2"));

        taskService.addTask(new Task());
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(
                        Task.fromMap(Utils.newHashMap(
                                "uuid", uuid1,
                                "string_with_default", "default_str",
                                "list_with_default", List.of("str1", "str2"),
                                "set_with_default", Set.of("str1", "str2"))))
        );

        propertyManager.setPropertyDescriptorCollection(new PropertyDescriptorCollection());
        initPropertyDescriptors();
    }

    @Test
    public void test_addTask_propertyNotExist() throws IOException {
        simpleTaskRepository.deleteAll();
        taskService.addTask(Task.fromMap(Utils.newHashMap("invalid_property", 123)));
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(Task.fromMap(Utils.newHashMap("invalid_property", 123, "uuid", uuid1)))
        );
    }

    @Test
    public void test_modifyTask() throws IOException, TaskServiceException {
        taskService.modifyTask(uuid2, Task.fromMap(Utils.newHashMap("done", false, "name", "do something")));
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap(
                "name", "test task", "assignees", Set.of("assignee1", "assignee2"),
                "uuid", uuid1, "tags", List.of("tag1", "tag3"))));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap(
                "name", "do something",
                "uuid", uuid2, "done", false, "tags", List.of("tag1", "tag2", "tag3"))));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap(
                "name", "yet another task", "uuid", uuid3,
                "tags", List.of("tag1", "tag2"))));
    }

    @Test
    public void test_modifyTask_empty() throws IOException, TaskServiceException {
        taskService.modifyTask(uuid2, new Task());
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap(
                "name", "test task", "assignees", Set.of("assignee1", "assignee2"),
                "uuid", uuid1, "tags", List.of("tag1", "tag3"))));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap(
                "name", "other task",
                "uuid", uuid2, "done", true,
                "tags", List.of("tag1", "tag2", "tag3"))));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap(
                "name", "yet another task", "uuid", uuid3,
                "tags", List.of("tag1", "tag2"))));
    }

    @Test
    public void test_modifyTask_propertyNotExist() throws IOException, TaskServiceException {
        taskService.modifyTask(uuid2, Task.fromMap(Utils.newHashMap("invalid_property", 123)));
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap(
                "name", "test task", "assignees", Set.of("assignee1", "assignee2"),
                "uuid", uuid1, "tags", List.of("tag1", "tag3"))));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap(
                "invalid_property", 123, "name", "other task",
                "uuid", uuid2, "done", true,
                "tags", List.of("tag1", "tag2", "tag3"))));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap(
                "name", "yet another task", "uuid", uuid3,
                "tags", List.of("tag1", "tag2"))));
    }

    @Test
    public void test_modifyTask_notExist() {
        assertThrows(TaskServiceException.class, () -> taskService.modifyTask(uuid4, Task.fromMap(Utils.newHashMap("done", false, "name", "do something"))));
    }

    @Test
    public void test_deleteTask() throws IOException, TaskServiceException {
        taskService.deleteTask(uuid2);
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    public void test_deleteTask_notExist() {
        assertThrows(TaskServiceException.class, () -> taskService.deleteTask(uuid4));
    }

    @Test
    public void test_getTaskTrees() throws PropertyException, IOException, PropertyConverterException, TaskServiceException {
        simpleTaskRepository.deleteAll();
        simpleTaskRepository.getTasks().addAll(List.of(
                Task.fromMap(Utils.newHashMap("name", "task1", "uuid", uuid1, "parent", uuid4)),
                Task.fromMap(Utils.newHashMap("name", "task2", "uuid", uuid2, "parent", uuid1)),
                Task.fromMap(Utils.newHashMap("name", "task3", "uuid", uuid3, "parent", uuid1)),
                Task.fromMap(Utils.newHashMap("name", "task4", "uuid", uuid4)),
                Task.fromMap(Utils.newHashMap("name", "task5", "uuid", uuid5)),
                Task.fromMap(Utils.newHashMap("name", "task6", "uuid", uuid6, "parent", uuid4)),
                Task.fromMap(Utils.newHashMap("name", "task7", "uuid", uuid7, "parent", uuid6)),
                Task.fromMap(Utils.newHashMap("name", "task8", "uuid", uuid8, "parent", uuid6))
        ));
        List<PropertyOwnerTree> taskHierarchies = taskService.getTaskTrees(null, new SortingInfo(List.of(new SortingCriterion("name", false))), null, null);
        assertEquals(taskHierarchies.size(), 2);
        assertEquals(taskHierarchies.get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task5", "uuid", uuid5)));
        assertEquals(taskHierarchies.get(0).getChildren().size(), 0);
        assertEquals(taskHierarchies.get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task4", "uuid", uuid4)));
        assertEquals(taskHierarchies.get(1).getChildren().size(), 2);
        {
            assertEquals(taskHierarchies.get(1).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task6", "uuid", uuid6, "parent", uuid4)));
            assertEquals(taskHierarchies.get(1).getChildren().get(0).getChildren().size(), 2);
            {
                assertEquals(taskHierarchies.get(1).getChildren().get(0).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task8", "uuid", uuid8, "parent", uuid6)));
                assertEquals(taskHierarchies.get(1).getChildren().get(0).getChildren().get(0).getChildren().size(), 0);
                assertEquals(taskHierarchies.get(1).getChildren().get(0).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task7", "uuid", uuid7, "parent", uuid6)));
                assertEquals(taskHierarchies.get(1).getChildren().get(0).getChildren().get(1).getChildren().size(), 0);
            }
            assertEquals(taskHierarchies.get(1).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task1", "uuid", uuid1, "parent", uuid4)));
            assertEquals(taskHierarchies.get(1).getChildren().get(1).getChildren().size(), 2);
            {
                assertEquals(taskHierarchies.get(1).getChildren().get(1).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task3", "uuid", uuid3, "parent", uuid1)));
                assertEquals(taskHierarchies.get(1).getChildren().get(1).getChildren().get(0).getChildren().size(), 0);
                assertEquals(taskHierarchies.get(1).getChildren().get(1).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task2", "uuid", uuid2, "parent", uuid1)));
                assertEquals(taskHierarchies.get(1).getChildren().get(1).getChildren().get(1).getChildren().size(), 0);
            }
        }
    }

    private <T> void assertListEquals(List<T> actual, List<T> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i), expected.get(i));
        }
    }

    private void initTasks() {
        simpleTaskRepository.deleteAll();
        simpleTaskRepository.getTasks().addAll(List.of(
                Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1,
                        "tags", List.of("tag1", "tag3"), "assignees", Set.of("assignee1", "assignee2"))),
                Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true, "tags", List.of("tag1", "tag2", "tag3"))),
                Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3, "tags", List.of("tag1", "tag2")))
        ));
    }

    private void initPropertyDescriptors() {
        addPropertyDescriptor("name", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
        addPropertyDescriptor("tags", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
        addPropertyDescriptor("assignees", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
        addPropertyDescriptor("done", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
        addPropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
        addPropertyDescriptor("parent", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
    }

    private void addPropertyDescriptor(
            String name,
            PropertyDescriptor.Type type,
            PropertyDescriptor.Multiplicity multiplicity,
            Object defaultValue) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                type, null, multiplicity, defaultValue, null));
    }

    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(10);
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();
    private final UUID uuid4 = uuidGenerator.getUUID();
    private final UUID uuid5 = uuidGenerator.getUUID();
    private final UUID uuid6 = uuidGenerator.getUUID();
    private final UUID uuid7 = uuidGenerator.getUUID();
    private final UUID uuid8 = uuidGenerator.getUUID();

    @InjectMocks
    private PropertyConverter propertyConverter;
    @Mock
    private TempIDMappingService tempIDMappingService;
    private TaskServiceImpl taskService;
    private PropertyManager propertyManager;
    private SimpleTaskRepository simpleTaskRepository;

}

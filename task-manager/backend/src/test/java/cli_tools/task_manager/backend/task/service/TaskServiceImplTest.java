package cli_tools.task_manager.backend.task.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.backend.property_converter.PropertyConverter;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.task_manager.backend.task.repository.SimpleTaskRepository;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class TaskServiceImplTest {

    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(10);
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
    private TempIDManager tempIdManager;
    private TaskServiceImpl taskService;
    private PropertyManager propertyManager;
    private SimpleTaskRepository simpleTaskRepository;

    @BeforeClass
    void init() {
        MockitoAnnotations.openMocks(this);

        simpleTaskRepository = new SimpleTaskRepository(uuidGenerator);
        SimpleTaskRepository doneSimpleTaskRepository = new SimpleTaskRepository(uuidGenerator);
        propertyManager = new PropertyManager();
        taskService = new TaskServiceImpl(
                simpleTaskRepository,
                doneSimpleTaskRepository,
                propertyManager,
                null,
                propertyConverter,
                tempIdManager
        );

        initPropertyDescriptors();
    }

    @BeforeMethod
    void beforeMethod() {
        uuidGenerator.reset();
        initTasks();
    }

    @Test
    void test_getTasks_all_empty() throws ServiceException {
        simpleTaskRepository.deleteAll();
        assertEquals(taskService.getTasks(true).size(), 0);
    }

    @Test
    void test_getTasks_all() throws ServiceException {
        List<Task> tasks = taskService.getTasks(true);

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
    void test_getTasks_uuids_empty() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null, null, List.of(), true);
        assertEquals(tasks.size(), 0);
    }

    @Test
    void test_getTasks_uuids_notExist() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid1, uuid4), false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
    }

    @Test
    void test_getTasks_uuids_duplicate() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid3, uuid1, uuid1, uuid1, uuid3), false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
    }

    @Test
    void test_getTasks_uuids() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null, null,
                List.of(uuid3, uuid1), false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_contains() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of("other"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("uuid")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of(uuid2.toString()))
                        .build(),
                null, false
        ));

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of())
                        .build(),
                null, false
        ));

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of("str1", "str2"))
                        .build(),
                null, false
        ));

        tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("tags")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of("tag1", "tag2"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_equals() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of("other task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");

        tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("tags")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of("tag1", "tag3"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");

        tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("assignees")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of("assignee1", "assignee2"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of("task2", "task1"))
                        .build(),
                null, false
        ));

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.EQUALS)
                        .operands(List.of())
                        .build(),
                null, false
        ));
    }

    @Test
    void test_getTasks_filterCriterionInfo_compare_invalid() {
        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("uuid")
                        .predicate(Predicate.LESS)
                        .operands(List.of(uuid2.toString()))
                        .build(),
                null, false
        ));

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.LESS)
                        .operands(List.of())
                        .build(),
                null, false
        ));

        assertThrows(ServiceException.class, () -> taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.LESS)
                        .operands(List.of("str1", "str2"))
                        .build(),
                null, false
        ));
    }

    @Test
    void test_getTasks_filterCriterionInfo_less() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.LESS)
                        .operands(List.of("test task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_lessEqual() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.LESS_EQUAL)
                        .operands(List.of("test task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_greater() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.GREATER)
                        .operands(List.of("test task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_greaterEqual() throws ServiceException {
        List<Task> tasks = taskService.getTasks(null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.GREATER_EQUAL)
                        .operands(List.of("test task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_in() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.IN)
                        .operands(List.of("test task", "other task"))
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_null() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("assignees")
                        .predicate(Predicate.NULL)
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_empty() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("assignees")
                        .predicate(Predicate.EMPTY)
                        .build(),
                null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_and() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.AND)
                        .children(List.of(
                                FilterCriterionInfo.builder()
                                        .name("test")
                                        .type(FilterCriterionInfo.Type.PROPERTY)
                                        .propertyName("name")
                                        .predicate(Predicate.IN)
                                        .operands(List.of("test task", "other task"))
                                        .build(),
                                FilterCriterionInfo.builder()
                                        .name("test")
                                        .type(FilterCriterionInfo.Type.PROPERTY)
                                        .propertyName("tags")
                                        .predicate(Predicate.CONTAINS)
                                        .operands(List.of("tag2")).build())).build(),
                null, false);
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");

        tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.OR)
                        .children(List.of(
                                FilterCriterionInfo.builder()
                                        .name("test")
                                        .type(FilterCriterionInfo.Type.PROPERTY)
                                        .propertyName("name")
                                        .predicate(Predicate.EQUALS)
                                        .operands(List.of("test task"))
                                        .build(),
                                FilterCriterionInfo.builder()
                                        .name("test")
                                        .type(FilterCriterionInfo.Type.PROPERTY)
                                        .propertyName("tags")
                                        .predicate(Predicate.CONTAINS)
                                        .operands(List.of("tag3")).build())).build(),
                null, false);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    void test_getTasks_filterCriterionInfo_not() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null, null,
                FilterCriterionInfo.builder()
                        .type(FilterCriterionInfo.Type.NOT)
                        .children(List.of(FilterCriterionInfo.builder()
                                .type(FilterCriterionInfo.Type.PROPERTY)
                                .propertyName("name")
                                .predicate(Predicate.EQUALS)
                                .operands(List.of("yet another task"))
                                .build())).build(),
                null, false);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "other task");
    }

    @Test
    void test_getTasks_filterPropertySpecs() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                List.of(
                        new FilterPropertySpec(propertyManager.getPropertyDescriptorCollection().get("name"), List.of("other"), false, Predicate.CONTAINS)
                ), null, null, null, false
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_sortingInfo() throws ServiceException {
        List<Task> tasks = taskService.getTasks(
                null,
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                null, null, false
        );
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0).getProperties().get("name"), "other task");
        assertEquals(tasks.get(1).getProperties().get("name"), "test task");
        assertEquals(tasks.get(2).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_getTasks_complex() throws ServiceException {
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
                FilterCriterionInfo.builder()
                        .name("test")
                        .type(FilterCriterionInfo.Type.PROPERTY)
                        .propertyName("name")
                        .predicate(Predicate.CONTAINS)
                        .operands(List.of("task"))
                        .build(),
                List.of(uuid1, uuid3, uuid4, uuid2), true
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3, "done", true)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
    }

    @Test
    void test_addTask() {
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
    void test_addTask_useDefault() {
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
    void test_addTask_propertyNotExist() {
        simpleTaskRepository.deleteAll();
        taskService.addTask(Task.fromMap(Utils.newHashMap("invalid_property", 123)));
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(Task.fromMap(Utils.newHashMap("invalid_property", 123, "uuid", uuid1)))
        );
    }

    @Test
    void test_modifyTask() throws ServiceException {
        taskService.modifyTask(Task.fromMap(Utils.newHashMap("uuid", uuid2, "done", false, "name", "do something")));
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
    void test_modifyTask_empty() throws ServiceException {
        taskService.modifyTask(Task.fromMap(Map.of("uuid", uuid2)));
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
    void test_modifyTask_propertyNotExist() throws ServiceException {
        taskService.modifyTask(Task.fromMap(Utils.newHashMap("uuid", uuid2, "invalid_property", 123)));
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
    void test_modifyTask_notExist() {
        assertThrows(TaskNotFoundException.class, () -> taskService.modifyTask(Task.fromMap(Utils.newHashMap("uuid", uuid4, "done", false, "name", "do something"))));
    }

    @Test
    void test_deleteTask() throws ServiceException {
        taskService.deleteTask(uuid2);
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getProperties().get("name"), "test task");
        assertEquals(tasks.get(1).getProperties().get("name"), "yet another task");
    }

    @Test
    void test_deleteTask_notExist() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(uuid4));
    }

    @Test
    void test_getTaskTrees() throws ServiceException {
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
        List<PropertyOwnerTree> taskTrees = taskService.getTaskTrees(null, new SortingInfo(List.of(new SortingCriterion("name", false))), null, null, true);
        assertEquals(taskTrees.size(), 2);
        assertEquals(taskTrees.get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task5", "uuid", uuid5)));
        assertEquals(taskTrees.get(0).getChildren().size(), 0);
        assertEquals(taskTrees.get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task4", "uuid", uuid4)));
        assertEquals(taskTrees.get(1).getChildren().size(), 2);
        {
            assertEquals(taskTrees.get(1).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task6", "uuid", uuid6, "parent", uuid4)));
            assertEquals(taskTrees.get(1).getChildren().get(0).getChildren().size(), 2);
            {
                assertEquals(taskTrees.get(1).getChildren().get(0).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task8", "uuid", uuid8, "parent", uuid6)));
                assertEquals(taskTrees.get(1).getChildren().get(0).getChildren().get(0).getChildren().size(), 0);
                assertEquals(taskTrees.get(1).getChildren().get(0).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task7", "uuid", uuid7, "parent", uuid6)));
                assertEquals(taskTrees.get(1).getChildren().get(0).getChildren().get(1).getChildren().size(), 0);
            }
            assertEquals(taskTrees.get(1).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task1", "uuid", uuid1, "parent", uuid4)));
            assertEquals(taskTrees.get(1).getChildren().get(1).getChildren().size(), 2);
            {
                assertEquals(taskTrees.get(1).getChildren().get(1).getChildren().get(0).getParent(), Task.fromMap(Utils.newHashMap("name", "task3", "uuid", uuid3, "parent", uuid1)));
                assertEquals(taskTrees.get(1).getChildren().get(1).getChildren().get(0).getChildren().size(), 0);
                assertEquals(taskTrees.get(1).getChildren().get(1).getChildren().get(1).getParent(), Task.fromMap(Utils.newHashMap("name", "task2", "uuid", uuid2, "parent", uuid1)));
                assertEquals(taskTrees.get(1).getChildren().get(1).getChildren().get(1).getChildren().size(), 0);
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

}

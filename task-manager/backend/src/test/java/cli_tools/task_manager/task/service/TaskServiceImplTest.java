package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.*;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.TaskHierarchy;
import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.filter.FilterCriterionException;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_converter.PropertyConverterException;
import common.task.repository.SimpleTaskRepository;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class TaskServiceImplTest {

    @BeforeClass public void init() {
        MockitoAnnotations.openMocks(this);

        simpleTaskRepository = new SimpleTaskRepository();
        propertyManager = new PropertyManager();
        taskUseCase = new TaskServiceImpl(
                simpleTaskRepository,
                propertyManager,
                null,
                propertyConverter,
                tempIDMappingService
        );

        initPropertyDescriptors();
    }

    @BeforeMethod
    public void resetUuidGenerator() {
        taskUseCase.setUuidGenerator(new RoundRobinUUIDGenerator(10));
    }

    @Test
    public void test_getTasks_all_empty() throws IOException {
        simpleTaskRepository.deleteAll();
        assertEquals(taskUseCase.getTasks().size(), 0);
    }

    @Test
    public void test_getTasks_all() throws IOException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_getTasks_uuids_empty() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null, null, null,
                List.of()
        );
        assertEquals(tasks.size(), 0);
    }

    @Test
    public void test_getTasks_uuids_notExist() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null, null, null,
                List.of(uuid1, uuid4)
        );
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
    }

    @Test
    public void test_getTasks_uuids_duplicate() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null, null, null,
                List.of(uuid3, uuid1, uuid1, uuid1, uuid3)
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
    }

    @Test
    public void test_getTasks_uuids() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null, null, null,
                List.of(uuid3, uuid1)
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
    }

    @Test
    public void test_getTasks_filterCriterionInfo() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null, null,
                new FilterCriterionInfo(
                        "test", FilterCriterionInfo.Type.PROPERTY,
                        "name", null,
                        Predicate.CONTAINS, List.of("other")
                ),
                null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_getTasks_filterPropertySpecs() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                List.of(
                        new FilterPropertySpec(Property.from(propertyManager.getPropertyDescriptorCollection().get("name"), "other"), false, Predicate.CONTAINS)
                ),
                null, null, null
        );
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_getTasks_sortingInfo() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        initTasks();
        List<Task> tasks = taskUseCase.getTasks(
                null,
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                null, null
        );
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_getTasks_complex() throws IOException, PropertyException, PropertyConverterException, FilterCriterionException, TaskServiceException {
        simpleTaskRepository.deleteAll();
        simpleTaskRepository.getTasks().addAll(List.of(
                Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)),
                Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)),
                Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3, "done", true)),
                Task.fromMap(Utils.newHashMap("name", "something else", "uuid", uuid3)),
                Task.fromMap(Utils.newHashMap("name", "whatever task", "uuid", uuid3, "done", true))
                ));
        List<Task> tasks = taskUseCase.getTasks(
                List.of(
                        new FilterPropertySpec(Property.from(propertyManager.getPropertyDescriptorCollection().get("done"), true), false, Predicate.EQUALS)
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
        taskUseCase.addTask(Task.fromMap(Utils.newHashMap("name", "test task")));
        taskUseCase.addTask(Task.fromMap(Utils.newHashMap("name", "other task", "done", true)));
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(
                        Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)),
                        Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true))
                )
        );
    }

    @Test
    public void test_addTask_propertyNotExist() throws IOException {
        simpleTaskRepository.deleteAll();
        taskUseCase.addTask(Task.fromMap(Utils.newHashMap("invalid_property", 123)));
        assertListEquals(
                simpleTaskRepository.getTasks(),
                List.of(Task.fromMap(Utils.newHashMap("invalid_property", 123, "uuid", uuid1)))
        );
    }

    @Test
    public void test_modifyTask() throws IOException, TaskServiceException {
        initTasks();
        taskUseCase.modifyTask(uuid2, Task.fromMap(Utils.newHashMap("done", false, "name", "do something")));
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "do something", "uuid", uuid2, "done", false)));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_modifyTask_empty() throws IOException, TaskServiceException {
        initTasks();
        taskUseCase.modifyTask(uuid2, new Task());
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_modifyTask_propertyNotExist() throws IOException, TaskServiceException {
        initTasks();
        taskUseCase.modifyTask(uuid2, Task.fromMap(Utils.newHashMap("invalid_property", 123)));
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 3);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "other task", "invalid_property", 123, "uuid", uuid2, "done", true)));
        assertEquals(tasks.get(2), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_modifyTask_notExist() {
        initTasks();
        assertThrows(TaskServiceException.class, () -> taskUseCase.modifyTask(uuid4, Task.fromMap(Utils.newHashMap("done", false, "name", "do something"))));
    }

    @Test
    public void test_deleteTask() throws IOException, TaskServiceException {
        initTasks();
        taskUseCase.deleteTask(uuid2);
        List<Task> tasks = simpleTaskRepository.getTasks();
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0), Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)));
        assertEquals(tasks.get(1), Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3)));
    }

    @Test
    public void test_deleteTask_notExist() {
        initTasks();
        assertThrows(TaskServiceException.class, () -> taskUseCase.deleteTask(uuid4));
    }

    @Test
    public void test_getTaskHierarchies() throws PropertyException, IOException, PropertyConverterException, FilterCriterionException, TaskServiceException {
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
        List<TaskHierarchy> taskHierarchies = taskUseCase.getTaskHierarchies(null, new SortingInfo(List.of(new SortingCriterion("name", false))), null, null);
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
                Task.fromMap(Utils.newHashMap("name", "test task", "uuid", uuid1)),
                Task.fromMap(Utils.newHashMap("name", "other task", "uuid", uuid2, "done", true)),
                Task.fromMap(Utils.newHashMap("name", "yet another task", "uuid", uuid3))
        ));
    }

    private void initPropertyDescriptors() {
        addPropertyDescriptor("name", PropertyDescriptor.Type.String);
        addPropertyDescriptor("done", PropertyDescriptor.Type.Boolean);
        addPropertyDescriptor("uuid", PropertyDescriptor.Type.UUID);
        addPropertyDescriptor("parent", PropertyDescriptor.Type.UUID);
    }

    private void addPropertyDescriptor(String name, PropertyDescriptor.Type type) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                type, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
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

    @InjectMocks private PropertyConverter propertyConverter;
    @Mock private TempIDMappingService tempIDMappingService;
    private TaskServiceImpl taskUseCase;
    private PropertyManager propertyManager;
    private SimpleTaskRepository simpleTaskRepository;

}

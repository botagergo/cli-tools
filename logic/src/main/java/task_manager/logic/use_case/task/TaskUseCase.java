package task_manager.logic.use_case.task;

import task_manager.core.data.SortingCriterion;
import task_manager.core.data.Task;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertySpec;
import task_manager.logic.PropertyNotComparableException;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.logic.use_case.view.PropertyConverterException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    Task addTask(Task task) throws IOException;

    Task modifyTask(Task task) throws IOException;

    boolean deleteTask(UUID uuid) throws IOException;

    List<Task> getTasks() throws IOException;

    List<Task> getTasks(String nameQuery, List<String> queries, List<PropertySpec> propertySpecs, List<SortingCriterion> sortingCriteria, String viewName) throws IOException, PropertyException, PropertyNotComparableException, PropertyConverterException, FilterCriterionException, TaskUseCaseException;

    Task getTask(UUID uuid) throws IOException;

    void deleteAllTasks() throws IOException;
}

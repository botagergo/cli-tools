package task_manager.logic.use_case.view;

import task_manager.core.data.Task;
import task_manager.logic.filter.FilterCriterion;
import task_manager.logic.sorter.PropertySorter;

public record View(String name, PropertySorter<Task> sorter, FilterCriterion filterCriterion) {}

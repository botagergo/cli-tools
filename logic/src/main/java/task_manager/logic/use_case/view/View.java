package task_manager.logic.use_case.view;

import task_manager.data.Task;
import task_manager.filter.FilterCriterion;
import task_manager.sorter.PropertySorter;

public record View(String name, PropertySorter<Task> sorter, FilterCriterion filterCriterion) {}

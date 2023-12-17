package task_manager.core.repository;

public interface OrderedLabelRepositoryFactory {

    OrderedLabelRepository getOrderedLabelRepository(String orderedLabelName);

}

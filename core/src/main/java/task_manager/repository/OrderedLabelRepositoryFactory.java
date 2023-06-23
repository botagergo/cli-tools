package task_manager.repository;

public interface OrderedLabelRepositoryFactory {

    OrderedLabelRepository getOrderedLabelRepository(String orderedLabelName);

}

package task_manager.core.repository;

public interface LabelRepositoryFactory {

    LabelRepository getLabelRepository(String labelName);

}

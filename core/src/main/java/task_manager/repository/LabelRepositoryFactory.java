package task_manager.repository;

public interface LabelRepositoryFactory {

    LabelRepository getLabelRepository(String labelName);

}

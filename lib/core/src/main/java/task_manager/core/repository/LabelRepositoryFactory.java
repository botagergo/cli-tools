package task_manager.core.repository;

import java.util.List;

public interface LabelRepositoryFactory {

    LabelRepository getLabelRepository(String labelType);

}

package task_manager.repository;

import java.io.File;

public class JsonLabelRepositoryFactory implements LabelRepositoryFactory{

    @Override
    public LabelRepository getLabelRepository(String labelName) {
        return new JsonLabelRepository(labelName, new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}

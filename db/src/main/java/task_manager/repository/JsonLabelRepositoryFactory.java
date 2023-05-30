package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NonNull;

import java.io.File;

public class JsonLabelRepositoryFactory implements LabelRepositoryFactory{

    @Inject
    public JsonLabelRepositoryFactory(@Named("basePath") @NonNull File basePath) {
        this.basePath = basePath;
    }

    @Override
    public LabelRepository getLabelRepository(String labelName) {
        return new JsonLabelRepository(labelName, basePath);
    }

    @NonNull private final File basePath;

}

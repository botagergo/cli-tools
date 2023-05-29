package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.File;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JsonLabelRepositoryFactory implements LabelRepositoryFactory{

    @Override
    public LabelRepository getLabelRepository(String labelName) {
        return new JsonLabelRepository(labelName, basePath);
    }

    @NonNull @Named("basePath") private final File basePath;

}

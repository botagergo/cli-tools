package task_manager.repository.label;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import task_manager.core.repository.LabelRepository;
import task_manager.core.repository.LabelRepositoryFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

@Singleton
public class JsonLabelRepositoryFactory implements LabelRepositoryFactory {

    @Inject
    public JsonLabelRepositoryFactory(@Named("basePath") @NonNull File basePath) {
        this.basePath = basePath;
    }

    @Override
    public LabelRepository getLabelRepository(String labelName) {
        return repositories.computeIfAbsent(labelName, (key) -> new JsonLabelRepository(Paths.get(basePath.toString(), labelName + ".json").toFile()));
    }

    @NonNull private final File basePath;
    private final HashMap<String, LabelRepository> repositories = new HashMap<>();

}

package cli_tools.common.label.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.core.repository.LabelRepositoryFactory;

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
    public LabelRepository getLabelRepository(String labelType) {
        return repositories.computeIfAbsent(labelType, (key) -> new JsonLabelRepository(Paths.get(basePath.toString(), labelType + ".json").toFile()));
    }

    @NonNull private final File basePath;
    private final HashMap<String, LabelRepository> repositories = new HashMap<>();

}

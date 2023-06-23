package task_manager.repository.ordered_label;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import task_manager.core.repository.OrderedLabelRepository;
import task_manager.core.repository.OrderedLabelRepositoryFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

@Singleton
public class JsonOrderedLabelRepositoryFactory implements OrderedLabelRepositoryFactory {

    @NonNull
    private final File basePath;
    private final HashMap<String, OrderedLabelRepository> repositories = new HashMap<>();

    @Inject
    public JsonOrderedLabelRepositoryFactory(@Named("basePath") @NonNull File basePath) {
        this.basePath = basePath;
    }

    @Override
    public OrderedLabelRepository getOrderedLabelRepository(String orderedLabelName) {
        return repositories.computeIfAbsent(orderedLabelName, (key) -> new JsonOrderedLabelRepository(Paths.get(basePath.toString(), orderedLabelName + ".json").toFile()));
    }

}

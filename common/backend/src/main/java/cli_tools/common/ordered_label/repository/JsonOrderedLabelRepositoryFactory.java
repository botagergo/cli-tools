package cli_tools.common.ordered_label.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import cli_tools.common.core.repository.OrderedLabelRepository;
import cli_tools.common.core.repository.OrderedLabelRepositoryFactory;

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
    public OrderedLabelRepository getOrderedLabelRepository(String orderedLabelType) {
        return repositories.computeIfAbsent(orderedLabelType, (key) -> new JsonOrderedLabelRepository(Paths.get(basePath.toString(), orderedLabelType + ".json").toFile()));
    }

}

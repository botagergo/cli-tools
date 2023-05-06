package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;

public class JsonTagRepository extends JsonLabelRepository {

    @Inject
    public JsonTagRepository(@Named("basePath") File basePath) {
        super("tags", basePath);
    }

}

package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;

public class JsonStatusRepository extends JsonLabelRepository {

    @Inject
    public JsonStatusRepository(@Named("basePath") File basePath) {
        super("statuses", basePath);
    }

}

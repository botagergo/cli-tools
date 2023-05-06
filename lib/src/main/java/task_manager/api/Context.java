package task_manager.api;

import com.google.inject.Inject;
import task_manager.api.use_case.StatusUseCase;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;

public class Context {

    public TaskUseCase getTaskUseCase() {
        return taskUseCase;
    }

    public StatusUseCase getStatusUseCase() {
        return statusUseCase;
    }

    public TagUseCase getTagUseCase() {
        return tagUseCase;
    }

    @Inject
    private TaskUseCase taskUseCase;

    @Inject
    private StatusUseCase statusUseCase;

    @Inject
    private TagUseCase tagUseCase;

}

package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.logic.use_case.StatusUseCase;
import task_manager.logic.use_case.TagUseCase;
import task_manager.logic.use_case.TaskUseCase;

public class Context {

    @Getter @Setter @Inject
    private TaskUseCase taskUseCase;

    @Getter @Setter @Inject
    private StatusUseCase statusUseCase;

    @Getter @Setter @Inject
    private TagUseCase tagUseCase;

}

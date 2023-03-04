package task_manager.api.command;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;

@Log4j2
public class ClearCommand implements Command {

    public ClearCommand() {
        this.taskUseCase = new TaskUseCase();
        this.tagUseCase = new TagUseCase();
    }

    @Override
    public void execute() {
        log.info("execute");
        try {
            taskUseCase.deleteAllTasks();
            tagUseCase.deleteAllTags();
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    TaskUseCase taskUseCase;
    TagUseCase tagUseCase;
}

package task_manager.api.command;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.Context;

@Log4j2
public class ClearCommand implements Command {

    @Override
    public void execute(Context context) {
        log.info("execute");
        try {
            context.getTaskUseCase().deleteAllTasks();
            context.getTagUseCase().deleteAllTags();
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}

package task_manager.ui.cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.ui.cli.Context;

import java.io.IOException;

@Log4j2
public class ClearCommand implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            context.getTaskUseCase().deleteAllTasks();
            context.getLabelUseCase().deleteAllLabels("tag");
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}

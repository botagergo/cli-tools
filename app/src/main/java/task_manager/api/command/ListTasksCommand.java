package task_manager.api.command;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import lombok.extern.log4j.Log4j2;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;
import task_manager.db.Tag;
import task_manager.db.Task;

@Log4j2
public class ListTasksCommand implements Command {

    public ListTasksCommand() {
        this.taskUseCase = new TaskUseCase();
        this.tagUseCase = new TagUseCase();
    }

    @Override
    public void execute() {
        log.info("execute");

        try {
            List<Task> tasks = taskUseCase.getTasks();
            for (Task task : tasks) {
                printTask(task);
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void printTask(Task task) throws IOException {
        String name = task.getName();

        Ansi done;
        if (task.getDone()) {
            done = Ansi.ansi().fg(Color.GREEN).a("\u2713").reset();
        } else {
            done = Ansi.ansi().a("\u2022");
        }
        System.out.format("%s %-32s%s\n", done, name, getTagsStr(task));
    }

    private String getTagsStr(Task task) throws IOException {
        String tagsStr = "";

        List<UUID> tagUuids = task.getTags();
        for (UUID tagUuid : tagUuids) {
            Tag tag = tagUseCase.getTag(tagUuid);

            if (tag != null) {
                tagsStr += "/" + tag.getName() + " ";
            }
        }

        return tagsStr;
    }

    TaskUseCase taskUseCase;
    TagUseCase tagUseCase;
}

package task_manager.api.command;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;
import task_manager.db.Tag;

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
            List<Map<String, Object>> tasks = taskUseCase.getTasks();
            for (Map<String, Object> task : tasks) {
                printTask(task);
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void printTask(Map<String, Object> task) throws IOException {
        String name = (String) task.get("name");
        if (task.containsKey("done") && task.get("done").equals(true)) {
            name = "\u2713 " + name;
        }
        System.out.format("%-32s%s\n", name, getTagsStr(task));
    }

    private String getTagsStr(Map<String, Object> task) throws IOException {
        String tagsStr = "";

        List<?> tagUuidStrs = (List<?>) task.get("tags");
        if (tagUuidStrs != null) {
            for (Object tagUuidStr : tagUuidStrs) {
                if (!(tagUuidStr instanceof String)) {
                    continue;
                }

                Tag tag = tagUseCase.getTag(UUID.fromString((String) tagUuidStr));

                if (tag != null) {
                    if (tagsStr.isEmpty()) {
                        tagsStr = "\ttags: ";
                    }
                    tagsStr += "/" + tag.getName() + " ";
                }
            }
        }

        return tagsStr;
    }

    TaskUseCase taskUseCase;
    TagUseCase tagUseCase;
}

package task_manager.api.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.use_case.StatusUseCase;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;
import task_manager.db.Status;
import task_manager.db.Tag;
import task_manager.db.Task;

@Log4j2
public class AddTaskCommand implements Command {

    public AddTaskCommand(String taskName, List<String> tagNames, String statusName) {
        this.name = taskName;
        this.tagNames = tagNames;
        this.statusName = statusName;
        this.tagUseCase = new TagUseCase();
        this.statusUseCase = new StatusUseCase();
        this.taskUseCase = new TaskUseCase();
    }

    @Override
    public void execute() {
        log.info("execute");

        try {
            Task task = new Task();

            task.setName(name);

            if (tagNames != null) {
                task.setTags(findTagUuids(tagNames));
            }

            if (statusName != null) {
                Status status = statusUseCase.findStatus(statusName);
                if (status != null) {
                    task.setStatus(status.getUuid());
                } else {
                    System.out.println("Invalid status: " + statusName);
                    return;
                }
            }

            taskUseCase.addTask(task);
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    private List<UUID> findTagUuids(List<String> tagNames) throws IOException {
        List<UUID> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagUseCase.findTag(tagName);

            if (tag == null) {
                // TODO System.out.println("Tag '" + tagStr + "' does not exist. Do you want to
                // create it? (Y/n)");
                tag = tagUseCase.addTag(tagName);
            }

            tags.add(tag.getUuid());
        }
        return tags;
    }

    TagUseCase tagUseCase;
    StatusUseCase statusUseCase;
    TaskUseCase taskUseCase;

    public String name;
    public List<String> tagNames;
    public String statusName;

}

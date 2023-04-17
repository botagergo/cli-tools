package task_manager.api.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.Context;
import task_manager.db.status.Status;
import task_manager.db.tag.Tag;
import task_manager.db.task.Task;

@Log4j2
public class AddTaskCommand implements Command {

    public AddTaskCommand(String taskName, List<String> tagNames, String statusName) {
        this.name = taskName;
        this.tagNames = tagNames;
        this.statusName = statusName;
    }

    @Override
    public void execute(Context context) {
        log.info("execute");

        try {
            Task task = new Task();

            task.setName(name);

            if (tagNames != null) {
                task.setTags(findTagUuids(context, tagNames));
            }

            if (statusName != null) {
                Status status = context.getStatusUseCase().findStatus(statusName);
                if (status != null) {
                    task.setStatus(status.getUuid());
                } else {
                    System.out.println("Invalid status: " + statusName);
                    return;
                }
            }

            context.getTaskUseCase().addTask(task);
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    private List<UUID> findTagUuids(Context context, List<String> tagNames) throws IOException {
        List<UUID> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = context.getTagUseCase().findTag(tagName);

            if (tag == null) {
                // TODO System.out.println("Tag '" + tagStr + "' does not exist. Do you want to
                // create it? (Y/n)");
                tag = context.getTagUseCase().addTag(tagName);
            }

            tags.add(tag.getUuid());
        }
        return tags;
    }

    public String name;
    public List<String> tagNames;
    public String statusName;

}

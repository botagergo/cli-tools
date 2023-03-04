package task_manager.api.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;
import task_manager.db.Tag;

@Log4j2
public class AddTaskCommand implements Command {

    public AddTaskCommand(String taskName, List<String> tagNames) {
        this.name = taskName;
        this.tagNames = tagNames;
        this.tagUseCase = new TagUseCase();
        this.taskUseCase = new TaskUseCase();
    }

    @Override
    public void execute() {
        log.info("execute");

        try {
            Map<String, Object> task = new HashMap<>();
            task.put("name", name);
            if (tagNames != null) {
                task.put("tags", findTagUuids(tagNames));
            }
            taskUseCase.addTask(task);
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private List<UUID> findTagUuids(List<String> tagNames) throws IOException {
        List<UUID> tags = new ArrayList<UUID>();
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
    TaskUseCase taskUseCase;

    public String name;
    public List<String> tagNames;

}

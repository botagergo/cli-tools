package task_manager.api.command;

import task_manager.db.TaskRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import task_manager.db.JsonTaskRepository;

public class DoneTaskCommand implements Command {

    public DoneTaskCommand(String query) {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
        this.query = query;
    }

    @Override
    public void execute() {
        try {
            List<Map<String, Object>> tasks = taskRepository.getTasks();
            List<Map<String, Object>> filteredTasks = tasks.stream().filter(
                    task -> {
                        return ((String) task.get("name")).toLowerCase().contains(this.query.toLowerCase());
                    }).collect(Collectors.toList());

            if (filteredTasks.size() == 0) {
                System.out.println("No task matches the string '" + query + "'");
            } else if (filteredTasks.size() > 1) {
                System.out.println("Multiple tasks match the string '" + query + "'");
            } else {
                Map<String, Object> task = filteredTasks.get(0);
                task.put("done", true);
                taskRepository.modifyTask(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TaskRepository taskRepository;
    public String query;
}

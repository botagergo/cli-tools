package task_manager;

import java.io.File;
import java.io.IOException;
import task_manager.db.property.JsonPropertyDescriptorRepository;
import task_manager.db.property.PropertyDescriptorRepository;
import task_manager.db.property.PropertyManager;
import task_manager.db.task.Task;
import task_manager.ui.cli.command_line.CommandLine;
import task_manager.ui.cli.command_line.JlineCommandLine;

public class App {

    public static void main(String[] args) throws IOException {
        PropertyDescriptorRepository repo = new JsonPropertyDescriptorRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));

        PropertyManager propManager = new PropertyManager(repo.getPropertyDescriptors());
        Task.setPropertyManager(propManager);
        CommandLine commandLine = new JlineCommandLine();
        commandLine.run();
    }
}

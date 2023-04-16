package task_manager.api.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import lombok.extern.log4j.Log4j2;
import task_manager.api.filter.AndFilterCriterion;
import task_manager.api.filter.ContainsCaseInsensitiveFilterCriterion;
import task_manager.api.filter.Filter;
import task_manager.api.filter.FilterCriterion;
import task_manager.api.filter.SimpleFilter;
import task_manager.api.filter.grammar.FilterBuilder;
import task_manager.api.use_case.StatusUseCase;
import task_manager.api.use_case.TagUseCase;
import task_manager.api.use_case.TaskUseCase;
import task_manager.db.property.PropertyException;
import task_manager.db.status.Status;
import task_manager.db.tag.Tag;
import task_manager.db.task.Task;

@Log4j2
public class ListTasksCommand implements Command {

    public ListTasksCommand(List<String> queries, String nameQuery) {
        this.queries = queries;
        this.nameQuery = nameQuery;

        this.taskUseCase = new TaskUseCase();
        this.tagUseCase = new TagUseCase();
        this.statusUseCase = new StatusUseCase();
    }

    @Override
    public void execute() {
        log.info("execute");

        try {
            List<Task> tasks = taskUseCase.getTasks();

            List<FilterCriterion> filterCriterions = new ArrayList<>();

            if (queries != null) {
                for (String query : queries) {
                    filterCriterions.add(FilterBuilder.buildFilter(query));
                }
            }

            if (nameQuery != null) {
                filterCriterions.add(new ContainsCaseInsensitiveFilterCriterion("name", nameQuery));
            }

            if (filterCriterions.size() != 0) {
                Filter filter = new SimpleFilter(new AndFilterCriterion(filterCriterions));
                tasks = filter.doFilter(tasks);
            }

            for (Task task : tasks) {
                printTask(task);
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}\n{}", e.toString(), ExceptionUtils.getStackTrace(e));
        }
    }

    private void printTask(Task task) throws IOException, PropertyException {
        String name = task.getName();

        Ansi done;
        if (task.getDone()) {
            done = Ansi.ansi().fg(Color.GREEN).a("\u2713").reset();
        } else {
            done = Ansi.ansi().a("\u2022");
        }

        System.out.format("%s %-32s%-15s%s\n", done, name, getStatusStr(task), getTagsStr(task));
    }

    private String getTagsStr(Task task) throws IOException, PropertyException {
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

    private String getStatusStr(Task task) throws IOException, PropertyException {
        UUID statusUuid = task.getStatus();
        if (statusUuid == null) {
            return "";
        }

        Status status = statusUseCase.getStatus(statusUuid);
        if (status == null) {
            log.warn("Status with UUID '" + statusUuid.toString() + "' does not exist");
            return "";
        }

        return status.getName();
    }

    public List<String> getQueries() {
        return queries;
    }

    public String getNameQuery() {
        return nameQuery;
    }
    
    private List<String> queries;
    private String nameQuery;

    TaskUseCase taskUseCase;
    TagUseCase tagUseCase;
    StatusUseCase statusUseCase;
}

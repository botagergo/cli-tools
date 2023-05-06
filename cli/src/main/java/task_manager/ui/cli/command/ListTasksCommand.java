package task_manager.ui.cli.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import lombok.extern.log4j.Log4j2;
import task_manager.data.Status;
import task_manager.data.Tag;
import task_manager.data.Task;
import task_manager.data.property.PropertyException;
import task_manager.filter.AndFilterCriterion;
import task_manager.filter.ContainsCaseInsensitiveFilterCriterion;
import task_manager.filter.Filter;
import task_manager.filter.FilterCriterion;
import task_manager.filter.SimpleFilter;
import task_manager.filter.grammar.FilterBuilder;
import task_manager.ui.cli.Context;

@Log4j2
public class ListTasksCommand implements Command {

    public ListTasksCommand(List<String> queries, String nameQuery) {
        this.queries = queries;
        this.nameQuery = nameQuery;
    }

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<Task> tasks = context.getTaskUseCase().getTasks();

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
                printTask(context, task);
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

    private void printTask(Context context, Task task) throws IOException, PropertyException {
        String name = task.getName();

        Ansi done;
        if (task.getDone()) {
            done = Ansi.ansi().fg(Color.GREEN).a("✓").reset();
        } else {
            done = Ansi.ansi().a("•");
        }

        System.out.format("%s %-32s%-15s%s\n", done, name, getStatusStr(context, task),
            getTagsStr(context, task));
    }

    private String getTagsStr(Context context, Task task) throws IOException, PropertyException {
        StringBuilder tagsStr = new StringBuilder();

        List<UUID> tagUuids = task.getTags();
        for (UUID tagUuid : tagUuids) {
            Tag tag = context.getTagUseCase().getTag(tagUuid);

            if (tag != null) {
                tagsStr.append("/").append(tag.name()).append(" ");
            }
        }

        return tagsStr.toString();
    }

    private String getStatusStr(Context context, Task task) throws IOException, PropertyException {
        UUID statusUuid = task.getStatus();
        if (statusUuid == null) {
            return "";
        }

        Status status = context.getStatusUseCase().getStatus(statusUuid);
        if (status == null) {
            log.warn("Status with UUID '" + statusUuid + "' does not exist");
            return "";
        }

        return status.name();
    }

    public List<String> getQueries() {
        return queries;
    }

    public String getNameQuery() {
        return nameQuery;
    }

    private final List<String> queries;
    private final String nameQuery;

}

package task_manager.ui.cli.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import lombok.extern.log4j.Log4j2;
import task_manager.data.Status;
import task_manager.data.Tag;
import task_manager.data.Task;
import task_manager.filter.*;
import task_manager.property.PropertyException;
import task_manager.property.PropertySpec;
import task_manager.sorter.PropertySorter;
import task_manager.ui.cli.Context;

@Log4j2
public record ListTasksCommand(
        List<String> queries, String nameQuery,
        List<PropertySorter.SortingCriterion> sortingCriteria,
        List<Triple<PropertySpec.Affinity, String, List<String>>> properties
) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            ArrayList<FilterCriterion> filterCriteria = null;

            if (properties != null) {
                List<PropertySpec> propertySpecs = context.getPropertyConverter().convertProperties(properties);
                filterCriteria = new ArrayList<>();
                for (PropertySpec propertySpec : propertySpecs) {
                    FilterCriterion filterCriterion = new EqualsFilterCriterion(
                            propertySpec.property().getPropertyDescriptor().name(),
                            propertySpec.property().getValue());
                    if (propertySpec.affinity() == PropertySpec.Affinity.NEGATIVE) {
                        filterCriterion = new NotFilterCriterion(filterCriterion);
                    }
                    filterCriteria.add(filterCriterion);
                }
            }

            List<Task> tasks = context.getTaskUseCase().getTasks(nameQuery, queries, filterCriteria);

            if (sortingCriteria != null && !sortingCriteria.isEmpty()) {
                PropertySorter<Task> sorter = new PropertySorter<>(sortingCriteria);
                tasks = sorter.sort(tasks, context.getPropertyManager());
            }

            for (Task task : tasks) {
                int tempID = context.getTempIDMappingRepository().getOrCreateID(task.getUUID());
                printTask(context, task, tempID);
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

    private void printTask(Context context, Task task, int tempID) throws IOException, PropertyException {
        String name = context.getPropertyManager().getProperty(task, "name").getString();

        Ansi done;
        if (context.getPropertyManager().getProperty(task, "done").getBoolean()) {
            done = Ansi.ansi().fg(Color.GREEN).a("✓").reset();
        } else {
            done = Ansi.ansi().a("•");
        }

        System.out.format("%s [%d] %-32s%-15s%s\n", done, tempID, name, getStatusStr(context, task),
                getTagsStr(context, task));
    }

    private String getTagsStr(Context context, Task task) throws IOException, PropertyException {
        StringBuilder tagsStr = new StringBuilder();

        LinkedHashSet<UUID> tagUuids = context.getPropertyManager().getProperty(task, "tags").getUuidSet();
        for (UUID tagUuid : tagUuids) {
            Tag tag = context.getTagUseCase().getTag(tagUuid);

            if (tag != null) {
                tagsStr.append("/").append(tag.name()).append(" ");
            }
        }

        return tagsStr.toString();
    }

    private String getStatusStr(Context context, Task task) throws IOException, PropertyException {
        UUID statusUuid = context.getPropertyManager().getProperty(task, "status").getUuid();
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

}

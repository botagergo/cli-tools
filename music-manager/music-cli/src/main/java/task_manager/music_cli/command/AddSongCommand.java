package common.music_cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import common.core.data.Task;
import common.core.property.ModifyPropertySpec;
import common.music_cli.Context;
import common.cli.argument.PropertyArgument;
import common.music_cli.command.property_modifier.PropertyModifier;

import java.util.List;

@Log4j2
public record AddSongCommand(String name, List<@NonNull PropertyArgument> modifyPropertyArgs) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            Task task = new Task();

            context.getPropertyManager().setProperty(task, "name", name);

            if (modifyPropertyArgs != null) {
                List<ModifyPropertySpec> propertySpecs = context.getStringToPropertyConverter().convertPropertiesForModification(modifyPropertyArgs, true);
                PropertyModifier.modifyProperties(context.getPropertyManager(), task, propertySpecs);
            }

            Task addedTask = context.getTaskUseCase().addTask(task);
            int tempID = context.getTempIDMappingUseCase().getOrCreateID(addedTask.getUUID());
            context.setGetPrevID(tempID);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

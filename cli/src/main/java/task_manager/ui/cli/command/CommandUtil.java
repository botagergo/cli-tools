package task_manager.ui.cli.command;

import lombok.NonNull;
import task_manager.core.property.FilterPropertySpec;
import task_manager.core.property.PropertyException;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.ui.cli.command.string_to_property_converter.StringToPropertyConverterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandUtil {
    public static List<UUID> getUUIDsFromTempIDs(@NonNull Context context, List<Integer> tempIDs) throws IOException {
        if (tempIDs != null && tempIDs.size() != 0) {
            List<UUID> taskUUIDs = new ArrayList<>();
            for (int tempID : tempIDs) {
                UUID uuid = context.getTempIDMappingRepository().getUUID(tempID);
                taskUUIDs.add(uuid);
            }
            return taskUUIDs;
        } else {
            return null;
        }
    }

    public static List<FilterPropertySpec> getFilterPropertySpecs(@NonNull Context context, List<PropertyArgument> filterPropertyArgs) throws StringToPropertyConverterException, PropertyException, IOException {
        if (filterPropertyArgs != null) {
            return context.getStringToPropertyConverter().convertPropertiesForFiltering(filterPropertyArgs, false);
        } else {
            return null;
        }
    }
}

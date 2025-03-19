package task_manager.logic.property_descriptor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import task_manager.logic.pseudo_property_provider.AliasPseudoPropertyProvider;
import task_manager.logic.pseudo_property_provider.TempIDPseudoPropertyProvider;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TempIDPseudoPropertyProvider.class, name = "TaskIDPseudoPropertyProvider"),
        @JsonSubTypes.Type(value = AliasPseudoPropertyProvider.class, name = "AliasPseudoPropertyProvider"),
})
public class PseudoPropertyProviderMixIn { }
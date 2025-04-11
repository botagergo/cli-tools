package cli_tools.task_manager.pseudo_property_provider;

import cli_tools.common.pseudo_property_provider.AliasPseudoPropertyProvider;
import cli_tools.common.pseudo_property_provider.TempIDPseudoPropertyProvider;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "provider")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TempIDPseudoPropertyProvider.class, name = "TempID"),
        @JsonSubTypes.Type(value = AliasPseudoPropertyProvider.class, name = "Alias"),
        @JsonSubTypes.Type(value = DonePseudoPropertyProvider.class, name = "Done"),
})
public class PseudoPropertyProviderMixIn {
}
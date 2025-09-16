package cli_tools.common.backend.property_descriptor.repository;

import cli_tools.common.backend.pseudo_property_provider.AliasPseudoPropertyProvider;
import cli_tools.common.backend.pseudo_property_provider.TempIDPseudoPropertyProvider;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TempIDPseudoPropertyProvider.class, name = "TaskIDPseudoPropertyProvider"),
        @JsonSubTypes.Type(value = AliasPseudoPropertyProvider.class, name = "AliasPseudoPropertyProvider"),
})
public class PseudoPropertyProviderMixIn {
}
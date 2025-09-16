package cli_tools.common.cli;

import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.property_to_string_converter.JsonPropertyToStringConverterRepository;
import cli_tools.common.cli.property_to_string_converter.MainPropertyToStringConverter;
import cli_tools.common.cli.string_to_property_converter.StringToPropertyConverter;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.backend.view.service.ViewInfoService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public abstract class Context {

    @Inject
    private LabelService labelService;

    @Inject
    private OrderedLabelService orderedLabelService;

    @Inject
    private PropertyDescriptorService propertyDescriptorService;

    @Inject
    private ViewInfoService viewInfoService;

    @Inject
    private PropertyManager propertyManager;

    @Inject
    private StringToPropertyConverter stringToPropertyConverter;

    @Inject
    private MainPropertyToStringConverter propertyToStringConverter;

    @Inject
    private TempIDManager tempIdManager;

    @Inject
    private JsonPropertyToStringConverterRepository propertyToStringConverterRepository;

    @Inject
    private ConfigurationRepository configurationRepository;

    @Inject
    private CommandParserFactory commandParserFactory;

    private Integer prevTempId = null;

}

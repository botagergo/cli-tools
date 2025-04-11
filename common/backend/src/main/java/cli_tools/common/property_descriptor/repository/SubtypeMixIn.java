package cli_tools.common.property_descriptor.repository;

import cli_tools.common.property_lib.PropertyDescriptor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "subtype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.LabelSubtype.class, name = "Label"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.OrderedLabelSubtype.class, name = "OrderedLabel"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.DateSubtype.class, name = "Date"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.TimeSubtype.class, name = "Time"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.TaskSubtype.class, name = "Task")
})
public record SubtypeMixIn() {
}

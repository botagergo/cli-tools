package cli_tools.common.property_descriptor.repository;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cli_tools.common.property_lib.PropertyDescriptor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.LabelSubtype.class, name = "PropertyDescriptor$Subtype$LabelSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.OrderedLabelSubtype.class, name = "PropertyDescriptor$Subtype$OrderedLabelSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.DateSubtype.class, name = "PropertyDescriptor$Subtype$DateSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.TimeSubtype.class, name = "PropertyDescriptor$Subtype$TimeSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.TaskSubtype.class, name = "PropertyDescriptor$Subtype$TaskSubtype")
})
public record SubtypeMixIn() { }

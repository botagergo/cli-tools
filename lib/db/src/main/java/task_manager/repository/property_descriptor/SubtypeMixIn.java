package task_manager.repository.property_descriptor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import task_manager.property_lib.PropertyDescriptor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.LabelSubtype.class, name = "PropertyDescriptor$Subtype$LabelSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.OrderedLabelSubtype.class, name = "PropertyDescriptor$Subtype$OrderedLabelSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.DateSubtype.class, name = "PropertyDescriptor$Subtype$DateSubtype"),
        @JsonSubTypes.Type(value = PropertyDescriptor.Subtype.TimeSubtype.class, name = "PropertyDescriptor$Subtype$TimeSubtype")
})
public record SubtypeMixIn() { }

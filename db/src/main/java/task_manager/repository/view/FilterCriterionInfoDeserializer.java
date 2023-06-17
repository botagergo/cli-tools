package task_manager.repository.view;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import task_manager.data.FilterCriterionInfo;

import java.io.IOException;

public class FilterCriterionInfoDeserializer extends JsonDeserializer<FilterCriterionInfo> {

    @Override
    public FilterCriterionInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        FilterCriterionInfo filterCriterionInfo = p.readValueAs(FilterCriterionInfo.class);

        if (filterCriterionInfo.type() == FilterCriterionInfo.Type.AND || filterCriterionInfo.type() == FilterCriterionInfo.Type.OR) {
            if (filterCriterionInfo.children() == null) {
                throw JsonMappingException.from(ctxt, "'children' field is required for AND/OR filter criterion");
            }
        } else if (filterCriterionInfo.type() == FilterCriterionInfo.Type.NOT ) {
            if (filterCriterionInfo.children() == null) {
                throw JsonMappingException.from(ctxt, "'children' field is required for NOT filter criterion");
            } else if (filterCriterionInfo.children().size() != 1) {
                throw JsonMappingException.from(ctxt, "NOT filter criterion must have exactly one child");
            }
        } else if (filterCriterionInfo.type() == FilterCriterionInfo.Type.PROPERTY) {
            if (filterCriterionInfo.propertyName() == null) {
                throw JsonMappingException.from(ctxt, "'propertyName' is required for property filter criterion");
            }
            if (filterCriterionInfo.predicate() == null) {
                throw JsonMappingException.from(ctxt, "'predicate' is required for property filter criterion");
            }
        }

        return filterCriterionInfo;
    }

}
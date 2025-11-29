package cli_tools.task_manager.cli.output_format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OutputFormat.GridOutputFormat.class, name = "Grid"),
        @JsonSubTypes.Type(value = OutputFormat.JsonOutputFormat.class, name = "Json")
})
public sealed interface OutputFormat {
    record GridOutputFormat(char intersectChar, char horizontalChar, char verticalChar) implements OutputFormat {
        public GridOutputFormat(
                @JsonProperty("intersectChar") char intersectChar,
                @JsonProperty("horizontalChar") char horizontalChar,
                @JsonProperty("verticalChar") char verticalChar) {
            this.intersectChar = intersectChar;
            this.horizontalChar = horizontalChar;
            this.verticalChar = verticalChar;
        }
    }

    record JsonOutputFormat(boolean isPretty) implements OutputFormat {
        @JsonCreator
        public JsonOutputFormat(@JsonProperty("pretty") boolean isPretty) {
            this.isPretty = isPretty;
        }
    }
}

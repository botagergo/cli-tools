package cli_tools.common.cli.command_line;

import cli_tools.common.cli.Context;
import lombok.extern.log4j.Log4j2;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import shadow.org.codehaus.plexus.util.ExceptionUtils;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;

@Log4j2
public class Completer implements org.jline.reader.Completer {

    public Completer(Context context, List<String> commands) {
        this.context = context;
        this.commands = commands;
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String word = parsedLine.word();

        int colonIndex = word.indexOf(':');
        if (colonIndex == -1) {
            List<String> properties;

            if (shouldCompleteCommands(parsedLine)) {
                for (String command : commands) {
                    list.add(buildCommandCandidate(command));
                }
            }

            try {
                properties = context.getPropertyDescriptorService().getPropertyDescriptors()
                        .stream().map(PropertyDescriptor::name).toList();
                for (String property : properties) {
                    list.add(buildPropertyCandidate(property));
                }
            } catch (IOException e) {
                log.error("Completer.complete - IOException: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
            }
        } else {
            String argName = word.substring(0, colonIndex);
            PropertyDescriptor propertyDescriptor;

            try {
                propertyDescriptor = context.getPropertyDescriptorService().findPropertyDescriptor(argName);
            } catch (PropertyException e) {
                return;
            } catch (IOException e) {
                log.error("Completer.complete - IOException: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
                return;
            }

            List<String> labelStrs = null;

            try {
                if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype labelSubtype) {
                    List<Label> labels = context.getLabelService().getLabels(labelSubtype.labelType());
                    labelStrs = labels.stream().map(Label::text).toList();
                } else if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype orderedLabelSubtype) {
                    List<OrderedLabel> labels = context.getOrderedLabelService().getOrderedLabels(orderedLabelSubtype.orderedLabelType());
                    labelStrs = labels.stream().map(OrderedLabel::text).toList();
                }
            } catch (IOException e) {
                log.error("Completer.complete - IOException: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
                return;
            }

            if (labelStrs != null) {
                String prefix = word.substring(0, parsedLine.wordCursor());
                int ind = prefix.length() - 1;
                while (ind > 0 && Character.isAlphabetic(prefix.charAt(ind))) {
                    ind--;
                }
                prefix = prefix.substring(0, ind + 1);
                for (String label : labelStrs) {
                    list.add(buildPropertyValueCandidate(prefix, label));
                }
            }
        }
    }

    private Candidate buildCommandCandidate(String name) {
        return new Candidate(name, name, "command", null, null, null, true);
    }

    private Candidate buildPropertyCandidate(String name) {
        return new Candidate(name, name, "property", null, null, null, false);
    }

    private Candidate buildPropertyValueCandidate(String prefix, String name) {
        return new Candidate(prefix + name, name, "property value", null, null, null, false);
    }

    private boolean shouldCompleteCommands(ParsedLine parsedLine) {
        for (int i = 0; i < parsedLine.wordIndex(); i++) {
            if (commands.contains(parsedLine.words().get(i))) {
                return false;
            }
        }
        return true;
    }

    private final Context context;
    private final List<String> commands;
}

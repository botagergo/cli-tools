package cli_tools.common.cli.command_line;

import cli_tools.common.cli.Context;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.util.Print;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import lombok.extern.log4j.Log4j2;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Log4j2
public class Completer implements org.jline.reader.Completer {

    private final Context context;
    private Collection<String> commands;

    public Completer(Context context) {
        this.context = context;
        this.commands = null;
    }

    private boolean isEscaped(String str, int pos) {
        if (pos > 1) {
            return str.charAt(pos - 1) == '\\' && str.charAt(pos - 2) != '\\';
        } else if (pos == 1) {
            return str.charAt(0) != '\\';
        } else {
            return false;
        }
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String word = parsedLine.word();

        int colonIndex = word.indexOf(':');
        while (colonIndex != -1 && isEscaped(word, colonIndex)) {
            colonIndex = word.indexOf(':', colonIndex + 1);
        }

        if (colonIndex != -1) {
            int secondColonIndex = word.indexOf(':', colonIndex + 1);
            while (secondColonIndex != -1 && isEscaped(word, secondColonIndex)) {
                secondColonIndex = word.indexOf(':', secondColonIndex + 1);
            }
            if (secondColonIndex != -1) {
                colonIndex = -1;
            }
        }

        int dotIndex = word.indexOf('.');
        if (dotIndex == 0) {
            dotIndex = -1;
        } else {
            while (dotIndex != -1 && isEscaped(word, dotIndex)) {
                dotIndex = word.indexOf('.', dotIndex + 1);
            }
        }

        if (dotIndex != -1) {
            int secondDotIndex = word.indexOf('.', dotIndex + 1);
            while (secondDotIndex != -1 && isEscaped(word, secondDotIndex)) {
                secondDotIndex = word.indexOf('.', secondDotIndex + 1);
            }
            if (secondDotIndex != -1) {
                dotIndex = -1;
            }
        }

        if (colonIndex != -1) {
            String propertyName;
            if (dotIndex != -1) {
                propertyName = getPropertyName(word, dotIndex);
            } else {
                propertyName = getPropertyName(word, colonIndex);
            }

            PropertyDescriptor propertyDescriptor;
            try {
                propertyDescriptor = context.getPropertyDescriptorService().findPropertyDescriptor(propertyName);
            } catch (PropertyException e) {
                return;
            } catch (IOException e) {
                Print.logException(e, log);
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
                Print.logException(e, log);
                return;
            }

            if (labelStrs != null) {
                String prefix = word.substring(0, parsedLine.wordCursor());
                int ind = prefix.length() - 1;
                while (ind > 0) {
                    char ch = prefix.charAt(ind);
                    if ((ch == ':' || ch == ',') && !isEscaped(prefix, ind)) {
                        break;
                    }

                    if (isAffinityChar(ch) && !isEscaped(prefix, ind) && !isEscaped(prefix, ind - 1)) {
                        ch = prefix.charAt(ind - 1);
                        if (ch == ':' || ch == ',') {
                            break;
                        }
                    }
                    ind--;
                }
                prefix = prefix.substring(0, ind + 1);
                for (String label : labelStrs) {
                    list.add(buildPropertyValueCandidate(prefix, label));
                }
            }
        } else if (dotIndex != -1) {
            String prefix = word.substring(0, dotIndex + 1);
            String propertyName = getPropertyName(word, dotIndex);

            try {
                PropertyDescriptor propertyDescriptor = context.getPropertyManager().getPropertyDescriptor(propertyName);
                for (Predicate predicate : Predicate.values()) {
                    if (predicate.isCompatibleWithProperty(propertyDescriptor)) {
                        list.add(buildPredicateCandidate(prefix, predicate.name().toLowerCase()));
                    }
                }
            } catch (PropertyException ignored) {
            }
        } else {
            List<String> properties;
            if (commands == null) {
                commands = context.getCommandParserFactory().getCommandNames();
            }

            if (shouldCompleteCommands(parsedLine)) {
                for (String command : commands) {
                    list.add(buildCommandCandidate(command));
                }
            }

            String prefix = "";
            if (parsedLine.wordCursor() > 0) {
                char ch = word.charAt(0);
                if (ch == '+' || ch == '-') {
                    prefix = Objects.toString(ch);
                }
            }

            try {
                properties = context.getPropertyDescriptorService().getPropertyDescriptors()
                        .stream().map(PropertyDescriptor::name).toList();
                for (String property : properties) {
                    list.add(buildPropertyCandidate(prefix, property));
                }
            } catch (IOException e) {
                Print.logException(e, log);
            }
        }
    }

    private String getPropertyName(String word, int endPos) {
        return word.substring(isAffinityChar(word.charAt(0)) ? 1 : 0, endPos);
    }

    private boolean isAffinityChar(char ch) {
        return ch == '+' || ch == '-';
    }

    private Candidate buildCommandCandidate(String name) {
        return new UnorderedCandidate(name, name, "command", null, null, null, true);
    }

    private Candidate buildPropertyCandidate(String prefix, String name) {
        return new UnorderedCandidate(prefix + name, name, "property", null, null, null, false);
    }

    private Candidate buildPropertyValueCandidate(String prefix, String name) {
        return new UnorderedCandidate(prefix + name, name, "property value", null, null, null, false);
    }

    private Candidate buildPredicateCandidate(String prefix, String name) {
        return new UnorderedCandidate(prefix + name, name, "predicate", null, null, null, false);
    }

    private boolean shouldCompleteCommands(ParsedLine parsedLine) {
        for (int i = 0; i < parsedLine.wordIndex(); i++) {
            if (commands.contains(parsedLine.words().get(i))) {
                return false;
            }
        }
        return true;
    }

    private static class UnorderedCandidate extends Candidate {
        public UnorderedCandidate(String value, String displ, String group, String descr, String suffix, String key, boolean complete) {
            super(value, displ, group, descr, suffix, key, complete);
        }

        @Override
        public int compareTo(org.jline.reader.Candidate candidate) {
            return 0;
        }
    }
}

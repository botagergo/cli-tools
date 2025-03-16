package task_manager.task_manager_cli.command_line;

import org.jline.reader.CompletingParsedLine;
import org.jline.reader.LineReader;
import org.jline.reader.impl.CompletionMatcherImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

class CompletionMatcher extends CompletionMatcherImpl {
    @Override
    protected void defaultMatchers(
            Map<LineReader.Option, Boolean> options,
            boolean prefix,
            CompletingParsedLine line,
            boolean caseInsensitive,
            int errors,
            String originalGroupName) {
        String wd = line.word();
        String wdi = caseInsensitive ? wd.toLowerCase() : wd;
        String wp = wdi.substring(0, line.wordCursor());
        matchers = new ArrayList<>(Collections.singletonList(
                simpleMatcher(s -> (caseInsensitive ? s.toLowerCase() : s).startsWith(wp))));
    }
}

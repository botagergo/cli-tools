package task_manager.ui.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexArgumentParser implements ArgumentParser {

    public RegexArgumentParser() {
        pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    }

    @Override
    public List<String> parse(String str) throws ArgumentParserException {
        ArrayList<String> args = new ArrayList<>();
        str = str.trim();

        if (str.chars().filter(ch -> ch == '\"').count() % 2 != 0) {
            throw new ArgumentParserException("Mismatched quotes");
        }

        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (match.length() >= 2
                    && match.charAt(0) == '\"'
                    && match.charAt(match.length() - 1) == '\"') {
                match = match.substring(1, match.length() - 1);
            }
            args.add(match);
        }

        return args;
    }

    Pattern pattern;

}

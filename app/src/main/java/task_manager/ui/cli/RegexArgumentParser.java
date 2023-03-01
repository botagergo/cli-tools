package task_manager.ui.cli;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import task_manager.ui.cli.command_parser.ArgumentList;

public class RegexArgumentParser implements ArgumentParser {

    public RegexArgumentParser() {
        pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
        specialArgumentChars = new HashSet<Character>(Arrays.asList(
        // TODO
        ));
    }

    @Override
    public ArgumentList parse(String str) throws ArgumentParserException {
        ArgumentList argList = new ArgumentList();

        str = str.trim();

        if (str.chars().filter(ch -> ch == '\"').count() % 2 != 0) {
            throw new ArgumentParserException("Mismatched quotes");
        }

        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String match = matcher.group(1);

            if (argList.commandName == null) {
                argList.commandName = match;
            } else {
                if (match.length() >= 2
                        && match.charAt(0) == '\"'
                        && match.charAt(match.length() - 1) == '\"') {
                    match = match.substring(1, match.length() - 1);
                }

                if (isSpecialArgument(match)) {
                    argList.specialArguments.add(match);
                } else {
                    argList.normalArguments.add(match);
                }
            }
        }

        return argList;
    }

    private boolean isSpecialArgument(String arg) {
        return arg.length() >= 1 && specialArgumentChars.contains(arg.charAt(0));
    }

    Pattern pattern;
    Set<Character> specialArgumentChars;

}

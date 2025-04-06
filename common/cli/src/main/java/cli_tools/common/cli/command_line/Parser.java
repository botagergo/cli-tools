package cli_tools.common.cli.command_line;

import org.jline.reader.impl.DefaultParser;

public class Parser extends DefaultParser {
    public Parser() {
        setEofOnEscapedNewLine(true);
        setEofOnUnclosedQuote(true);
    }

    @Override
    public boolean isDelimiterChar(CharSequence buffer, int pos) {
        return super.isDelimiterChar(buffer, pos) || buffer.charAt(pos) == '+' || buffer.charAt(pos) == '-';
    }

    @Override
    public boolean validCommandName(String name) {
        return true;
    }
}

package task_manager.task_manager_cli.command_line;

import org.jline.reader.impl.DefaultParser;

class Parser extends DefaultParser {

    @Override
    public boolean isDelimiterChar(CharSequence buffer, int pos) {
        return super.isDelimiterChar(buffer, pos) || buffer.charAt(pos) == '+' || buffer.charAt(pos) == '-';
    }

    @Override
    public boolean validCommandName(String name) {
        return true;
    }
}

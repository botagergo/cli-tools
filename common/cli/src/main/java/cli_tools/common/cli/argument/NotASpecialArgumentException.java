package cli_tools.common.cli.argument;

public class NotASpecialArgumentException extends Exception {
    public NotASpecialArgumentException(String arg) {
        super(arg);
    }
}

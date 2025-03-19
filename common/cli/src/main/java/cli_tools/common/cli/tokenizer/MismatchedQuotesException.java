package cli_tools.common.cli.tokenizer;

public class MismatchedQuotesException extends Exception {

    public MismatchedQuotesException(String line) {
        super("Mismatched quotes: " + line);
    }

}

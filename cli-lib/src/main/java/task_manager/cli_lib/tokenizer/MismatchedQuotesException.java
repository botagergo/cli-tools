package task_manager.cli_lib.tokenizer;

public class MismatchedQuotesException extends Exception {

    public MismatchedQuotesException(String line) {
        super("Mismatched quotes: " + line);
    }

}

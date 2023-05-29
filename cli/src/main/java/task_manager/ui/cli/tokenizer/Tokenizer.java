package task_manager.ui.cli.tokenizer;

public interface Tokenizer {

    TokenList tokenize(String line) throws MismatchedQuotesException;

}

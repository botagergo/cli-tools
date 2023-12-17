package task_manager.cli_lib.tokenizer;

public interface Tokenizer {

    TokenList tokenize(String line) throws MismatchedQuotesException;

}

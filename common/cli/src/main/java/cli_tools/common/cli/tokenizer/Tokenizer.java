package cli_tools.common.cli.tokenizer;

public interface Tokenizer {

    TokenList tokenize(String line) throws MismatchedQuotesException;

}

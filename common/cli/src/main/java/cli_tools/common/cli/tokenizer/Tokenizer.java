package cli_tools.common.cli.tokenizer;

import java.util.List;

public interface Tokenizer {

    List<String> tokenize(String line) throws MismatchedQuotesException;

}

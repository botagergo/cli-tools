package cli_tools.common.cli.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class TokenizerImpl implements Tokenizer {

    @Override
    public List<String> tokenize(String line) throws MismatchedQuotesException {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        char currentQuote = 0;
        boolean isEscaped = false;

        int i = 0;
        while (i < line.length()) {
            char currentChar = line.charAt(i);

            if (!isEscaped && currentChar == '\\') {
                isEscaped = true;
                currentToken.append(currentChar);
            } else if (isEscaped) {
                currentToken.append(currentChar);
                isEscaped = false;
            } else if (currentChar == '\'' || currentChar == '"') {
                if (currentQuote == 0) {
                    currentQuote = currentChar;
                } else if (currentChar == currentQuote) {
                    currentQuote = 0;
                }
                currentToken.append(currentChar);
            } else if (currentQuote == 0 && Character.isWhitespace(currentChar)) {
                addToken(tokens, currentToken);
            } else {
                currentToken.append(currentChar);
            }

            i += 1;
        }

        addToken(tokens, currentToken);

        if (currentQuote != 0) {
            throw new MismatchedQuotesException(line);
        }

        return tokens;
    }

    private void addToken(ArrayList<String> tokens, StringBuilder token) {
        if (!token.isEmpty()) {
            tokens.add(token.toString());
            token.setLength(0);
        }
    }

}

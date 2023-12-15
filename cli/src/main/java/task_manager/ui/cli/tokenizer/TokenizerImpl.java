package task_manager.ui.cli.tokenizer;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TokenizerImpl implements Tokenizer {

    @Override
    public TokenList tokenize(String line) throws MismatchedQuotesException {
        ArrayList<String> tokens = new ArrayList<>();
        Set<Pair<Integer, Integer>> escapedPositions = new HashSet<>();
        StringBuilder currentToken = new StringBuilder();

        char currentQuote = 0;
        boolean isEscaped = false;

        int i = 0;
        while (i < line.length()) {
            char currentChar = line.charAt(i);

            if (!isEscaped && currentChar == '\\') {
                isEscaped = true;
            } else if (isEscaped) {
                escapedPositions.add(Pair.of(tokens.size(), currentToken.length()));
                currentToken.append(currentChar);
                isEscaped = false;
            } else if (currentChar == '\'' || currentChar == '"') {
                if (currentQuote == 0) {
                    currentQuote = currentChar;
                } else if (currentChar == currentQuote) {
                    currentQuote = 0;
                } else {
                    currentToken.append(currentChar);
                }
            } else if (currentQuote == 0) {
                if (Character.isWhitespace(currentChar)) {
                    addToken(tokens, currentToken);
                } else {
                    currentToken.append(currentChar);
                }
            } else {
                currentToken.append(currentChar);
            }

            i += 1;
        }

        addToken(tokens, currentToken);

        if (currentQuote != 0) {
            throw new MismatchedQuotesException(line);
        }

        return new TokenList(tokens, escapedPositions);
    }

    private void addToken(ArrayList<String> tokens, StringBuilder token) {
        if (!token.isEmpty()) {
            tokens.add(token.toString());
            token.setLength(0);
        }
    }

}

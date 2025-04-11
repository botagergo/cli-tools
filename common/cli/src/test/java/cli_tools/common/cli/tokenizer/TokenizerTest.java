package cli_tools.common.cli.tokenizer;

import cli_tools.common.cli.TestModule;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

@Guice(modules = TestModule.class)
public class TokenizerTest {

    private final Tokenizer tokenizer = new TokenizerImpl();

    @Test
    void testEmpty() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("").size(), 0);
    }

    @Test
    void testBlank() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" \t\n\r\n").size(), 0);
    }

    @Test
    void testSingle() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test"), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize("test  "), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize(" test"), List.of("test"));

    }

    @Test
    void testTwo() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test one"), List.of("test", "one"));
        Assert.assertEquals(tokenizer.tokenize(" test  two  "), List.of("test", "two"));
        Assert.assertEquals(tokenizer.tokenize(" test three"), List.of("test", "three"));
    }

    @Test
    void testMultiple() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" some long   string "), List.of("some", "long", "string"));
    }

    @Test
    void testSingleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test'"), List.of("'test'"));
        Assert.assertEquals(tokenizer.tokenize("'test   '"), List.of("'test   '"));
    }

    @Test
    void testDoubleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\"test\""), List.of("\"test\""));
        Assert.assertEquals(tokenizer.tokenize("\"test   \""), List.of("\"test   \""));
    }

    @Test
    void testQuoteInsideQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\"'test   \""), List.of("\"'test   \""));
        Assert.assertEquals(tokenizer.tokenize("\"'test '  \""), List.of("\"'test '  \""));
        Assert.assertEquals(tokenizer.tokenize(" \"'test '  \""), List.of("\"'test '  \""));
        Assert.assertEquals(tokenizer.tokenize(" \"' test '  \""), List.of("\"' test '  \""));
        Assert.assertEquals(tokenizer.tokenize("'\"test   '"), List.of("'\"test   '"));
        Assert.assertEquals(tokenizer.tokenize("'\"test \"  '"), List.of("'\"test \"  '"));
        Assert.assertEquals(tokenizer.tokenize(" '\"test \"  '"), List.of("'\"test \"  '"));
        Assert.assertEquals(tokenizer.tokenize(" '\" test \"  '"), List.of("'\" test \"  '"));
    }

    @Test
    void testMixedQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test' \"one\""), List.of("'test'", "\"one\""));
        Assert.assertEquals(tokenizer.tokenize("\"test\" 'two'"), List.of("\"test\"", "'two'"));
    }

    @Test
    void testQuotesInsideToken() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("hello' world'"), List.of("hello' world'"));
        Assert.assertEquals(tokenizer.tokenize("th'is'\" is a \"'funn'y string"), List.of("th'is'\" is a \"'funn'y", "string"));
    }

    @Test
    void testEscape() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\\'test"), List.of("\\'test"));
        Assert.assertEquals(tokenizer.tokenize("\\\"test"), List.of("\\\"test"));
        Assert.assertEquals(tokenizer.tokenize("test\\ string"), List.of("test\\ string"));
    }

    @Test
    void testNewline() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("hello\\nworld"), List.of("hello", "world"));
        Assert.assertEquals(tokenizer.tokenize("hello\\r\\n\\nworld"), List.of("hello", "world"));
        Assert.assertEquals(tokenizer.tokenize("'hello\\nworld'"), List.of("'hello\nworld'"));
        Assert.assertEquals(tokenizer.tokenize("'hello\\r\\nworld'"), List.of("'hello\r\nworld'"));
        Assert.assertEquals(tokenizer.tokenize("hello\\\nworld"), List.of("hello", "world"));
        Assert.assertEquals(tokenizer.tokenize("hello\\\r\\\nworld"), List.of("hello", "world"));
    }

    @Test
    void testMismatchedQuotes() {
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("'"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("\""));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world\""));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello \"world'"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello '''world"));
    }

}

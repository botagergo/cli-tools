package cli_tools.common.cli.tokenizer;

import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import cli_tools.common.cli.TestModule;

import java.util.List;

@Guice(modules = TestModule.class)
public class TokenizerTest {

    @Test
    public void testEmpty() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("").size(), 0);
    }

    @Test
    public void testBlank() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" \t\n\r\n").size(), 0);
    }

    @Test
    public void testSingle() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test"), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize("test  "), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize(" test"), List.of("test"));

    }

    @Test
    public void testTwo() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test one"), List.of("test", "one"));
        Assert.assertEquals(tokenizer.tokenize(" test  two  "), List.of("test", "two"));
        Assert.assertEquals(tokenizer.tokenize(" test three"), List.of("test", "three"));
    }

    @Test
    public void testMultiple() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" some long   string "), List.of("some", "long", "string"));
    }

    @Test
    public void testSingleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test'"), List.of("'test'"));
        Assert.assertEquals(tokenizer.tokenize("'test   '"), List.of("'test   '"));
    }

    @Test
    public void testDoubleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\"test\""), List.of("\"test\""));
        Assert.assertEquals(tokenizer.tokenize("\"test   \""), List.of("\"test   \""));
    }

    @Test
    public void testQuoteInsideQuotes() throws MismatchedQuotesException {
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
    public void testMixedQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test' \"one\""), List.of("'test'", "\"one\""));
        Assert.assertEquals(tokenizer.tokenize("\"test\" 'two'"), List.of("\"test\"", "'two'"));
    }

    @Test
    public void testQuotesInsideToken() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("hello' world'"), List.of("hello' world'"));
        Assert.assertEquals(tokenizer.tokenize("th'is'\" is a \"'funn'y string"), List.of("th'is'\" is a \"'funn'y", "string"));
    }

    @Test
    public void testEscape() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\\'test"), List.of("\\'test"));
        Assert.assertEquals(tokenizer.tokenize("\\\"test"), List.of("\\\"test"));
        Assert.assertEquals(tokenizer.tokenize("test\\ string"), List.of("test\\ string"));
    }

    @Test
    public void testMismatchedQuotes() {
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("'"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("\""));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world\""));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello \"world'"));
        Assert.assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello '''world"));
    }

    private final Tokenizer tokenizer = new TokenizerImpl();

}

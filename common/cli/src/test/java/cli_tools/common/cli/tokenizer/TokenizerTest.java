package cli_tools.common.cli.tokenizer;

import jakarta.inject.Inject;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import cli_tools.common.cli.TestModule;

import java.util.List;

@Guice(modules = TestModule.class)
public class TokenizerTest {

    @Test
    public void testEmpty() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("").tokens().size(), 0);
    }

    @Test
    public void testBlank() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" \t\n\r\n").tokens().size(), 0);
    }

    @Test
    public void testSingle() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test").tokens(), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize("test  ").tokens(), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize(" test").tokens(), List.of("test"));

    }

    @Test
    public void testTwo() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("test one").tokens(), List.of("test", "one"));
        Assert.assertEquals(tokenizer.tokenize(" test  two  ").tokens(), List.of("test", "two"));
        Assert.assertEquals(tokenizer.tokenize(" test three").tokens(), List.of("test", "three"));
    }

    @Test
    public void testMultiple() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize(" some long   string ").tokens(), List.of("some", "long", "string"));
    }

    @Test
    public void testSingleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test'").tokens(), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize("'test   '").tokens(), List.of("test   "));
    }

    @Test
    public void testDoubleQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\"test\"").tokens(), List.of("test"));
        Assert.assertEquals(tokenizer.tokenize("\"test   \"").tokens(), List.of("test   "));
    }

    @Test
    public void testQuoteInsideQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\"'test   \"").tokens(), List.of("'test   "));
        Assert.assertEquals(tokenizer.tokenize("\"'test '  \"").tokens(), List.of("'test '  "));
        Assert.assertEquals(tokenizer.tokenize(" \"'test '  \"").tokens(), List.of("'test '  "));
        Assert.assertEquals(tokenizer.tokenize(" \"' test '  \"").tokens(), List.of("' test '  "));
        Assert.assertEquals(tokenizer.tokenize("'\"test   '").tokens(), List.of("\"test   "));
        Assert.assertEquals(tokenizer.tokenize("'\"test \"  '").tokens(), List.of("\"test \"  "));
        Assert.assertEquals(tokenizer.tokenize(" '\"test \"  '").tokens(), List.of("\"test \"  "));
        Assert.assertEquals(tokenizer.tokenize(" '\" test \"  '").tokens(), List.of("\" test \"  "));
    }

    @Test
    public void testMixedQuotes() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("'test' \"one\"").tokens(), List.of("test", "one"));
        Assert.assertEquals(tokenizer.tokenize("\"test\" 'two'").tokens(), List.of("test", "two"));
    }

    @Test
    public void testQuotesInsideToken() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("hello' world'").tokens(), List.of("hello world"));
        Assert.assertEquals(tokenizer.tokenize("th'is'\" is a \"'funn'y string").tokens(), List.of("this is a funny", "string"));
    }

    @Test
    public void testEscape() throws MismatchedQuotesException {
        Assert.assertEquals(tokenizer.tokenize("\\'test").tokens(), List.of("'test"));
        Assert.assertEquals(tokenizer.tokenize("\\\"test").tokens(), List.of("\"test"));
        Assert.assertEquals(tokenizer.tokenize("test\\ string").tokens(), List.of("test string"));
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

    @Inject
    private Tokenizer tokenizer;

}

package task_manager.ui.cli.tokenizer;

import jakarta.inject.Inject;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import task_manager.ui.cli.TestModule;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@Guice(modules = TestModule.class)
public class TokenizerTest {

    @Test
    public void testEmpty() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("").tokens().size(), 0);
    }

    @Test
    public void testBlank() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize(" \t\n\r\n").tokens().size(), 0);
    }

    @Test
    public void testSingle() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("test").tokens(), List.of("test"));
        assertEquals(tokenizer.tokenize("test  ").tokens(), List.of("test"));
        assertEquals(tokenizer.tokenize(" test").tokens(), List.of("test"));

    }

    @Test
    public void testTwo() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("test one").tokens(), List.of("test", "one"));
        assertEquals(tokenizer.tokenize(" test  two  ").tokens(), List.of("test", "two"));
        assertEquals(tokenizer.tokenize(" test three").tokens(), List.of("test", "three"));
    }

    @Test
    public void testMultiple() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize(" some long   string ").tokens(), List.of("some", "long", "string"));
    }

    @Test
    public void testSingleQuotes() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("'test'").tokens(), List.of("test"));
        assertEquals(tokenizer.tokenize("'test   '").tokens(), List.of("test   "));
    }

    @Test
    public void testDoubleQuotes() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("\"test\"").tokens(), List.of("test"));
        assertEquals(tokenizer.tokenize("\"test   \"").tokens(), List.of("test   "));
    }

    @Test
    public void testQuoteInsideQuotes() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("\"'test   \"").tokens(), List.of("'test   "));
        assertEquals(tokenizer.tokenize("\"'test '  \"").tokens(), List.of("'test '  "));
        assertEquals(tokenizer.tokenize(" \"'test '  \"").tokens(), List.of("'test '  "));
        assertEquals(tokenizer.tokenize(" \"' test '  \"").tokens(), List.of("' test '  "));
        assertEquals(tokenizer.tokenize("'\"test   '").tokens(), List.of("\"test   "));
        assertEquals(tokenizer.tokenize("'\"test \"  '").tokens(), List.of("\"test \"  "));
        assertEquals(tokenizer.tokenize(" '\"test \"  '").tokens(), List.of("\"test \"  "));
        assertEquals(tokenizer.tokenize(" '\" test \"  '").tokens(), List.of("\" test \"  "));
    }

    @Test
    public void testMixedQuotes() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("'test' \"one\"").tokens(), List.of("test", "one"));
        assertEquals(tokenizer.tokenize("\"test\" 'two'").tokens(), List.of("test", "two"));
    }

    @Test
    public void testQuotesInsideToken() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("hello' world'").tokens(), List.of("hello world"));
        assertEquals(tokenizer.tokenize("th'is'\" is a \"'funn'y string").tokens(), List.of("this is a funny", "string"));
    }

    @Test
    public void testEscape() throws MismatchedQuotesException {
        assertEquals(tokenizer.tokenize("\\'test").tokens(), List.of("'test"));
        assertEquals(tokenizer.tokenize("\\\"test").tokens(), List.of("\"test"));
        assertEquals(tokenizer.tokenize("test\\ string").tokens(), List.of("test string"));
    }

    @Test
    public void testMismatchedQuotes() {
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("'"));
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("\""));
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world"));
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello 'world\""));
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello \"world'"));
        assertThrows(MismatchedQuotesException.class, () -> tokenizer.tokenize("hello '''world"));
    }

    @Inject
    private Tokenizer tokenizer;

}

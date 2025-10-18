package cli_tools.test_utils;

import org.testng.Assert;

import static org.testng.Assert.assertEquals;

public class AssertUtils {
    public static void assertJsonEquals(String actual, String expected) {
        assertEquals(actual.replaceAll("\\s", ""), expected.replaceAll("\\s", ""));
    }
}

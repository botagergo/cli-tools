package task_manager.cli_lib;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public class DateTimeParserTest {

    @Test
    public void test_parseLocalDate_standard() {
        assertParseDate("08-11-1999", LocalDate.of(1999, 8, 11)); // MM-dd-uuuu
        assertParseDate("20-08-1999", LocalDate.of(1999, 8, 20)); // dd-MM-uuuu
        assertParseDate("2020-04-12", LocalDate.of(2020, 4, 12)); //uuuu-MM-dd

        assertParseDate("10-08", LocalDate.of(2020, 10, 8)); // MM-dd
        assertParseDate("Jun-08", LocalDate.of(2020, 6, 8)); // MMM-dd
        assertParseDate("June-08", LocalDate.of(2020, 6, 8)); // MMMM-dd
        assertParseDate("Jun-8", LocalDate.of(2020, 6, 8)); // MMM-d
        assertParseDate("June-8", LocalDate.of(2020, 6, 8)); // MMMM-d

        assertParseDate("Jun", LocalDate.of(2020, 6, 30)); // MMM
        assertParseDate("June", LocalDate.of(2020, 6, 30)); // MMMM
        assertParseDate("February", LocalDate.of(2020, 2, 29)); // MMMM

        assertParseDate("2022", LocalDate.of(2022, 12, 31)); // yyyy
    }

    @Test
    public void test_parseLocalDate_standard_invalidDate() {
        Assert.assertThrows(DateTimeParseException.class, () -> dateTimeParser.parseLocalDate("08/11/1999"));
        Assert.assertThrows(DateTimeParseException.class, () -> dateTimeParser.parseLocalDate("June 8"));
    }

    @Test
    public void test_parseLocalDate_custom_singleNoun() {
        assertParseDate("today", LocalDate.of(2020, 11, 3));
        assertParseDate("yesterday", LocalDate.of(2020, 11, 2));
        assertParseDate("tomorrow", LocalDate.of(2020, 11, 4));
        assertParseDate("monday", LocalDate.of(2020, 11, 9));
        assertParseDate("tuesday", LocalDate.of(2020, 11, 3));
        assertParseDate("wednesday", LocalDate.of(2020, 11, 4));
        assertParseDate("thursday", LocalDate.of(2020, 11, 5));
        assertParseDate("friday", LocalDate.of(2020, 11, 6));
        assertParseDate("saturday", LocalDate.of(2020, 11, 7));
        assertParseDate("sunday", LocalDate.of(2020, 11, 8));
    }

    @Test
    public void test_parseLocalDate_custom_withQuantity_singular() {
        assertParseDate("+3day", LocalDate.of(2020, 11, 6));
        assertParseDate("-4day", LocalDate.of(2020, 10, 30));
        assertParseDate("+2week", LocalDate.of(2020, 11, 17));
        assertParseDate("-1week", LocalDate.of(2020, 10, 27));
        assertParseDate("+2month", LocalDate.of(2021, 1, 3));
        assertParseDate("-2month", LocalDate.of(2020, 9, 3));
        assertParseDate("+4year", LocalDate.of(2024, 11, 3));
        assertParseDate("-2year", LocalDate.of(2018, 11, 3));
        assertParseDate("+2monday", LocalDate.of(2020, 11, 16));
        assertParseDate("-2monday", LocalDate.of(2020, 10, 26));
        assertParseDate("+2tuesday", LocalDate.of(2020, 11, 17));
        assertParseDate("-2tuesday", LocalDate.of(2020, 10, 20));
        assertParseDate("+2wednesday", LocalDate.of(2020, 11, 11));
        assertParseDate("-2wednesday", LocalDate.of(2020, 10, 21));
        assertParseDate("+2thursday", LocalDate.of(2020, 11, 12));
        assertParseDate("-2thursday", LocalDate.of(2020, 10, 22));
        assertParseDate("+2friday", LocalDate.of(2020, 11, 13));
        assertParseDate("-2friday", LocalDate.of(2020, 10, 23));
        assertParseDate("+2saturday", LocalDate.of(2020, 11, 14));
        assertParseDate("-2saturday", LocalDate.of(2020, 10, 24));
        assertParseDate("+2sunday", LocalDate.of(2020, 11, 15));
        assertParseDate("-2sunday", LocalDate.of(2020, 10, 25));
    }

    @Test
    public void test_parseLocalDate_custom_withQuantity_plural() {
        assertParseDate("+3days", LocalDate.of(2020, 11, 6));
        assertParseDate("-4days", LocalDate.of(2020, 10, 30));
        assertParseDate("+2weeks", LocalDate.of(2020, 11, 17));
        assertParseDate("-1weeks", LocalDate.of(2020, 10, 27));
        assertParseDate("+2months", LocalDate.of(2021, 1, 3));
        assertParseDate("-2months", LocalDate.of(2020, 9, 3));
        assertParseDate("+4years", LocalDate.of(2024, 11, 3));
        assertParseDate("-2years", LocalDate.of(2018, 11, 3));
        assertParseDate("+2mondays", LocalDate.of(2020, 11, 16));
        assertParseDate("-2mondays", LocalDate.of(2020, 10, 26));
        assertParseDate("+2tuesdays", LocalDate.of(2020, 11, 17));
        assertParseDate("-2tuesdays", LocalDate.of(2020, 10, 20));
        assertParseDate("+2wednesdays", LocalDate.of(2020, 11, 11));
        assertParseDate("-2wednesdays", LocalDate.of(2020, 10, 21));
        assertParseDate("+2thursdays", LocalDate.of(2020, 11, 12));
        assertParseDate("-2thursdays", LocalDate.of(2020, 10, 22));
        assertParseDate("+2fridays", LocalDate.of(2020, 11, 13));
        assertParseDate("-2fridays", LocalDate.of(2020, 10, 23));
        assertParseDate("+2saturdays", LocalDate.of(2020, 11, 14));
        assertParseDate("-2saturdays", LocalDate.of(2020, 10, 24));
        assertParseDate("+2sundays", LocalDate.of(2020, 11, 15));
        assertParseDate("-2sundays", LocalDate.of(2020, 10, 25));
    }

    @Test
    public void test_parseLocalDate_custom_upperCase() {
        assertParseDate("TODAY", LocalDate.of(2020, 11, 3));
        assertParseDate("Yesterday", LocalDate.of(2020, 11, 2));
        assertParseDate("ToMoRrOw", LocalDate.of(2020, 11, 4));
        assertParseDate("+3dAys", LocalDate.of(2020, 11, 6));
    }

    @Test
    public void test_parseLocalDate_custom_withWhitespace() {
        assertParseDate(" today ", LocalDate.of(2020, 11, 3));
        assertParseDate("+ 3 days", LocalDate.of(2020, 11, 6));
        assertParseDate(" - 2    weeks ", LocalDate.of(2020, 10, 20));
    }

    @Test
    public void test_parseLocalDate_custom_invalidNoun() {
        assertThrows("  ");
        assertThrows("");
        assertThrows("invalid");
    }

    @Test
    public void test_parseLocalDate_custom_withQuantity_invalidNoun() {
        assertThrows("+3");
        assertThrows("+");
        assertThrows("-");
        assertThrows("-4invalid");
    }

    @Test void test_parseLocalDate_custom_invalidQuantity() {
        assertThrows("+0days");
        assertThrows("-0days");
        assertThrows("-1.5days");
        assertThrows("+3.93days");
        assertThrows("+days");
        assertThrows("-days");
    }

    private void assertParseDate(String date, LocalDate expected) {
        Assert.assertEquals(dateTimeParser.parseLocalDate(date), expected);
    }

    private void assertThrows(String date) {
        Assert.assertThrows(DateTimeParseException.class, () -> {dateTimeParser.parseLocalDate(date);});
    }

    private final DateTimeParser dateTimeParser = new DateTimeParser(Clock.fixed(Instant.from(
            LocalDate.of(2020, 11, 3).atStartOfDay(ZoneId.of("Europe/Budapest"))), ZoneId.of("Europe/Budapest")));

}

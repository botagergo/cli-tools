package cli_tools.common.cli;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.*;
import java.time.format.DateTimeParseException;

public class DateTimeParserTest {

    private final DateTimeParser dateTimeParser = new DateTimeParser(Clock.fixed(Instant.from(
            LocalDateTime.of(2020, 11, 3, 19, 45, 22).atZone(ZoneId.of("Europe/Budapest"))), ZoneId.of("Europe/Budapest")));

    @Test
    public void test_parseLocalDate_standard() {
        assertParseLocalDate("08-11-1999", LocalDate.of(1999, 8, 11)); // MM-dd-uuuu
        assertParseLocalDate("20-08-1999", LocalDate.of(1999, 8, 20)); // dd-MM-uuuu
        assertParseLocalDate("2020-04-12", LocalDate.of(2020, 4, 12)); //uuuu-MM-dd

        assertParseLocalDate("10-08", LocalDate.of(2020, 10, 8)); // MM-dd
        assertParseLocalDate("Jun-08", LocalDate.of(2020, 6, 8)); // MMM-dd
        assertParseLocalDate("June-08", LocalDate.of(2020, 6, 8)); // MMMM-dd
        assertParseLocalDate("Jun-8", LocalDate.of(2020, 6, 8)); // MMM-d
        assertParseLocalDate("June-8", LocalDate.of(2020, 6, 8)); // MMMM-d

        assertParseLocalDate("Jun", LocalDate.of(2020, 6, 30)); // MMM
        assertParseLocalDate("June", LocalDate.of(2020, 6, 30)); // MMMM
        assertParseLocalDate("February", LocalDate.of(2020, 2, 29)); // MMMM

        assertParseLocalDate("2022", LocalDate.of(2022, 12, 31)); // yyyy
    }

    @Test
    public void test_parseLocalDate_standard_invalidDate() {
        assertParseLocalDateThrows("08/11/1999");
        assertParseLocalDateThrows("June 8");
    }

    @Test
    public void test_parseLocalDate_custom_singleNoun() {
        assertParseLocalDate("today", LocalDate.of(2020, 11, 3));
        assertParseLocalDate("yesterday", LocalDate.of(2020, 11, 2));
        assertParseLocalDate("tomorrow", LocalDate.of(2020, 11, 4));
        assertParseLocalDate("monday", LocalDate.of(2020, 11, 9));
        assertParseLocalDate("tuesday", LocalDate.of(2020, 11, 3));
        assertParseLocalDate("wednesday", LocalDate.of(2020, 11, 4));
        assertParseLocalDate("thursday", LocalDate.of(2020, 11, 5));
        assertParseLocalDate("friday", LocalDate.of(2020, 11, 6));
        assertParseLocalDate("saturday", LocalDate.of(2020, 11, 7));
        assertParseLocalDate("sunday", LocalDate.of(2020, 11, 8));
    }

    @Test
    public void test_parseLocalDate_custom_withQuantity_singular() {
        assertParseLocalDate("+3day", LocalDate.of(2020, 11, 6));
        assertParseLocalDate("-4day", LocalDate.of(2020, 10, 30));
        assertParseLocalDate("+2week", LocalDate.of(2020, 11, 17));
        assertParseLocalDate("-1week", LocalDate.of(2020, 10, 27));
        assertParseLocalDate("+2month", LocalDate.of(2021, 1, 3));
        assertParseLocalDate("-2month", LocalDate.of(2020, 9, 3));
        assertParseLocalDate("+4year", LocalDate.of(2024, 11, 3));
        assertParseLocalDate("-2year", LocalDate.of(2018, 11, 3));
        assertParseLocalDate("+2monday", LocalDate.of(2020, 11, 16));
        assertParseLocalDate("-2monday", LocalDate.of(2020, 10, 26));
        assertParseLocalDate("+2tuesday", LocalDate.of(2020, 11, 17));
        assertParseLocalDate("-2tuesday", LocalDate.of(2020, 10, 20));
        assertParseLocalDate("+2wednesday", LocalDate.of(2020, 11, 11));
        assertParseLocalDate("-2wednesday", LocalDate.of(2020, 10, 21));
        assertParseLocalDate("+2thursday", LocalDate.of(2020, 11, 12));
        assertParseLocalDate("-2thursday", LocalDate.of(2020, 10, 22));
        assertParseLocalDate("+2friday", LocalDate.of(2020, 11, 13));
        assertParseLocalDate("-2friday", LocalDate.of(2020, 10, 23));
        assertParseLocalDate("+2saturday", LocalDate.of(2020, 11, 14));
        assertParseLocalDate("-2saturday", LocalDate.of(2020, 10, 24));
        assertParseLocalDate("+2sunday", LocalDate.of(2020, 11, 15));
        assertParseLocalDate("-2sunday", LocalDate.of(2020, 10, 25));
    }

    @Test
    public void test_parseLocalDate_custom_withQuantity_plural() {
        assertParseLocalDate("+3days", LocalDate.of(2020, 11, 6));
        assertParseLocalDate("-4days", LocalDate.of(2020, 10, 30));
        assertParseLocalDate("+2weeks", LocalDate.of(2020, 11, 17));
        assertParseLocalDate("-1weeks", LocalDate.of(2020, 10, 27));
        assertParseLocalDate("+2months", LocalDate.of(2021, 1, 3));
        assertParseLocalDate("-2months", LocalDate.of(2020, 9, 3));
        assertParseLocalDate("+4years", LocalDate.of(2024, 11, 3));
        assertParseLocalDate("-2years", LocalDate.of(2018, 11, 3));
        assertParseLocalDate("+2mondays", LocalDate.of(2020, 11, 16));
        assertParseLocalDate("-2mondays", LocalDate.of(2020, 10, 26));
        assertParseLocalDate("+2tuesdays", LocalDate.of(2020, 11, 17));
        assertParseLocalDate("-2tuesdays", LocalDate.of(2020, 10, 20));
        assertParseLocalDate("+2wednesdays", LocalDate.of(2020, 11, 11));
        assertParseLocalDate("-2wednesdays", LocalDate.of(2020, 10, 21));
        assertParseLocalDate("+2thursdays", LocalDate.of(2020, 11, 12));
        assertParseLocalDate("-2thursdays", LocalDate.of(2020, 10, 22));
        assertParseLocalDate("+2fridays", LocalDate.of(2020, 11, 13));
        assertParseLocalDate("-2fridays", LocalDate.of(2020, 10, 23));
        assertParseLocalDate("+2saturdays", LocalDate.of(2020, 11, 14));
        assertParseLocalDate("-2saturdays", LocalDate.of(2020, 10, 24));
        assertParseLocalDate("+2sundays", LocalDate.of(2020, 11, 15));
        assertParseLocalDate("-2sundays", LocalDate.of(2020, 10, 25));
    }

    @Test
    public void test_parseLocalDate_custom_upperCase() {
        assertParseLocalDate("TODAY", LocalDate.of(2020, 11, 3));
        assertParseLocalDate("Yesterday", LocalDate.of(2020, 11, 2));
        assertParseLocalDate("ToMoRrOw", LocalDate.of(2020, 11, 4));
        assertParseLocalDate("+3dAys", LocalDate.of(2020, 11, 6));
    }

    @Test
    public void test_parseLocalDate_custom_withWhitespace() {
        assertParseLocalDate(" today ", LocalDate.of(2020, 11, 3));
        assertParseLocalDate("+ 3 days", LocalDate.of(2020, 11, 6));
        assertParseLocalDate(" - 2    weeks ", LocalDate.of(2020, 10, 20));
    }

    @Test
    public void test_parseLocalDate_custom_singleNoun_invalidNoun() {
        assertParseLocalDateThrows("  ");
        assertParseLocalDateThrows("");
        assertParseLocalDateThrows("invalid");
    }

    @Test
    void test_parseLocalDate_custom_withQuantity_invalidNoun() {
        assertParseLocalDateThrows("+3");
        assertParseLocalDateThrows("+");
        assertParseLocalDateThrows("-");
        assertParseLocalDateThrows("-4invalid");
    }

    @Test
    public void test_parseLocalDate_custom_separatedByWhitespace() {
        assertParseLocalDateThrows("to day");
        assertParseLocalDateThrows("w ednesday");
        assertParseLocalDateThrows("+3 4days");
    }

    @Test
    void test_parseLocalDate_custom_withQuantiy_invalidQuantity() {
        assertParseLocalDateThrows("+0days");
        assertParseLocalDateThrows("-0days");
        assertParseLocalDateThrows("-1.5days");
        assertParseLocalDateThrows("+3.93days");
        assertParseLocalDateThrows("+days");
        assertParseLocalDateThrows("-days");
    }

    @Test
    void test_parseLocalTime_standard() {
        assertParseLocalTime("3:15 PM", LocalTime.of(15, 15)); // h:mm a
        assertParseLocalTime("3:15 pm", LocalTime.of(15, 15));
        assertParseLocalTime("3:15 Pm", LocalTime.of(15, 15));
        assertParseLocalTime("3:15 pM", LocalTime.of(15, 15));
        assertParseLocalTime("3:15 AM", LocalTime.of(3, 15));
        assertParseLocalTime("3:15 am", LocalTime.of(3, 15));
        assertParseLocalTime("3:15 Am", LocalTime.of(3, 15));
        assertParseLocalTime("3:15 aM", LocalTime.of(3, 15));
        assertParseLocalTime("19:33", LocalTime.of(19, 33)); // HH:mm
        assertParseLocalTime("3 PM", LocalTime.of(15, 0)); // h a
        assertParseLocalTime("3 AM", LocalTime.of(3, 0));
        assertParseLocalTime("3AM", LocalTime.of(3, 0)); // ha
        assertParseLocalTime("3pm", LocalTime.of(15, 0));
    }

    @Test
    public void test_parseLocalTime_standard_invalidTime() {
        assertParseLocalTimeThrows("3:34");
        assertParseLocalTimeThrows("14:34 PM");
    }

    @Test
    void test_parseLocalTime_custom_singleNoun() {
        assertParseLocalTime("now", LocalTime.of(19, 45, 22));
        assertParseLocalTime("+2hour", LocalTime.of(21, 45, 22));
    }

    @Test
    public void test_parseLocalTime_custom_withQuantity_singular() {
        assertParseLocalTime("+2hour", LocalTime.of(21, 45, 22));
        assertParseLocalTime("-4hour", LocalTime.of(15, 45, 22));
        assertParseLocalTime("+10minute", LocalTime.of(19, 55, 22));
        assertParseLocalTime("-20minute", LocalTime.of(19, 25, 22));
        assertParseLocalTime("+30second", LocalTime.of(19, 45, 52));
        assertParseLocalTime("-2second", LocalTime.of(19, 45, 20));
    }

    @Test
    public void test_parseLocalTime_custom_withQuantity_plural() {
        assertParseLocalTime("+2hours", LocalTime.of(21, 45, 22));
        assertParseLocalTime("-4hours", LocalTime.of(15, 45, 22));
        assertParseLocalTime("+10minutes", LocalTime.of(19, 55, 22));
        assertParseLocalTime("-20minutes", LocalTime.of(19, 25, 22));
        assertParseLocalTime("+30seconds", LocalTime.of(19, 45, 52));
        assertParseLocalTime("-2seconds", LocalTime.of(19, 45, 20));
    }

    @Test
    public void test_parseLocalTime_custom_upperCase() {
        assertParseLocalTime("+2HOURS", LocalTime.of(21, 45, 22));
        assertParseLocalTime("Now", LocalTime.of(19, 45, 22));
        assertParseLocalTime("+10minuTEs", LocalTime.of(19, 55, 22));
    }

    @Test
    public void test_parseLocalTime_custom_withWhitespace() {
        assertParseLocalTime("+ 2 hours", LocalTime.of(21, 45, 22));
        assertParseLocalTime("  now ", LocalTime.of(19, 45, 22));
        assertParseLocalTime("+10   minuTEs ", LocalTime.of(19, 55, 22));
    }

    @Test
    public void test_parseLocalTime_custom_singleNoun_invalidNoun() {
        assertParseLocalTimeThrows("invalid");
        assertParseLocalTimeThrows("");
        assertParseLocalTimeThrows("  ");
    }

    @Test
    void test_parseLocalTime_custom_withQuantity_invalidNoun() {
        assertParseLocalTimeThrows("+3");
        assertParseLocalTimeThrows("+");
        assertParseLocalTimeThrows("-");
        assertParseLocalTimeThrows("-4invalid");
    }

    @Test
    public void test_parseLocalTime_custom_separatedByWhitespace() {
        assertParseLocalDateThrows("n ow");
        assertParseLocalDateThrows("+2 3hours");
    }

    @Test
    public void test_parseLocalTime_custom_withQuantity_invalidQuantity() {
        assertParseLocalTimeThrows("+0hours");
        assertParseLocalTimeThrows("-0hours");
        assertParseLocalTimeThrows("-1.5hours");
        assertParseLocalTimeThrows("+3.93hours");
        assertParseLocalTimeThrows("+hours");
        assertParseLocalTimeThrows("-hours");
    }

    private void assertParseLocalDate(String date, LocalDate expected) {
        Assert.assertEquals(dateTimeParser.parseLocalDate(date), expected);
    }

    private void assertParseLocalDateThrows(String date) {
        Assert.assertThrows(DateTimeParseException.class, () -> dateTimeParser.parseLocalDate(date));
    }

    private void assertParseLocalTime(String time, LocalTime expected) {
        Assert.assertEquals(dateTimeParser.parseLocalTime(time), expected);
    }

    private void assertParseLocalTimeThrows(String time) {
        Assert.assertThrows(DateTimeParseException.class, () -> dateTimeParser.parseLocalTime(time));
    }

}

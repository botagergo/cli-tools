package task_manager.cli_lib;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateTimeFormatterTest {
    @Test
    public void test_formatLocalDate() {
        assertFormatDate(LocalDate.of(2021, 11, 30), "2021-11-30");
        assertFormatDate(LocalDate.of(2020, 3, 12), "2020-03-12");
        assertFormatDate(LocalDate.of(2040, 2, 29), "2040-02-29");
        assertFormatDate(LocalDate.of(13, 12, 31), "0013-12-31");
        assertFormatDate(LocalDate.of(0, 1, 1), "0000-01-01");
        assertFormatDate(LocalDate.of(9999, 1, 1), "9999-01-01");
    }

    @Test
    public void test() {
        java.time.format.DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .append(java.time.format.DateTimeFormatter.ofPattern("[yyyy-MM-dd]"+ "[MMM-dd]"))
                .parseDefaulting(ChronoField.YEAR, 2020).toFormatter();
        dateTimeFormatter.parse("2023-11-04");
    }

    @Test
    public void test_formatLocalTime() {
        assertFormatTime(LocalTime.of(10, 31, 11), "10 AM");
        assertFormatTime(LocalTime.of(12, 31, 11), "12 PM");
        assertFormatTime(LocalTime.of(14, 20, 11), "2 PM");
        assertFormatTime(LocalTime.of(23, 59, 59), "11 PM");
        assertFormatTime(LocalTime.of(0, 1, 2), "12 AM");
    }

    private void assertFormatDate(LocalDate localDate, String expected) {
        Assert.assertEquals(dateTimeFormatter.formatLocalDate(localDate), expected);
    }

    private void assertFormatTime(LocalTime localTime, String expected) {
        Assert.assertEquals(dateTimeFormatter.formatLocalTime(localTime), expected);
    }

    private final DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

}

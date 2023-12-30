package task_manager.cli_lib;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeFormatter {

    public String formatLocalTime(LocalTime localTime) {
        return localTime.format(java.time.format.DateTimeFormatter.ofPattern("h a"));
    }

    public String formatLocalDate(LocalDate localDate) {
        return localDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
    }

}

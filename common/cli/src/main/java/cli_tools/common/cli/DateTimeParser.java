package cli_tools.common.cli;

import lombok.extern.log4j.Log4j2;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DateTimeParser {

    public DateTimeParser() {
        this(Clock.systemDefaultZone());
    }

    public DateTimeParser(Clock clock) {
        this.clock = clock;

        dateFormatterBuilders = new ArrayList<>();
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MM-dd-uuuu")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("dd-MM-uuuu")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("uuuu-MM-dd")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MM-dd")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMM-dd")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMMM-dd")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMM-d")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMMM-d")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMM"))
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 31));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("MMMM"))
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 31));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("d")));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("dd"))
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 31));
        dateFormatterBuilders.add(new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("yyyy"))
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 12)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 31));

        timeFormatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern(
                        "[h:mm a]" + "[HH:mm]" + "[h a]" + "[ha]"))
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).toFormatter();
        pattern = Pattern.compile("^\\s*([+\\-])\\s*([1-9][0-9]*)\\s*(\\w+?)s?\\s*$");
    }

    public LocalDate parseLocalDate(String dateStr) throws DateTimeParseException {
        LocalDate now = LocalDate.now(clock);
        LocalDate parsed = parseLocalDateStandard(now, dateStr);
        if (parsed != null) {
            return parsed;
        } else {
            return parseLocalDateCustom(now, dateStr);
        }
    }

    public LocalTime parseLocalTime(String timeStr) throws DateTimeParseException {
        try {
            return parseLocalTimeStandard(timeStr);
        } catch (DateTimeParseException e) {
            return parseLocalTimeCustom(timeStr);
        }
    }

    private LocalDate parseLocalDateStandard(LocalDate now, String date) {
        for (int i = 3; i <= 11; i++) {
            dateFormatterBuilders.set(i, dateFormatterBuilders.get(i).parseDefaulting(ChronoField.YEAR, now.getYear()));

            if (i == 9 || i == 10) {
                dateFormatterBuilders.get(i).parseLenient();
            }

            if (i >= 10) {
                dateFormatterBuilders.get(i).parseDefaulting(ChronoField.YEAR, now.getMonthValue());
            }
        }

        for (DateTimeFormatterBuilder dateTimeFormatterBuilder : dateFormatterBuilders) {
            java.time.format.DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
            try {
                return LocalDate.parse(date, dateTimeFormatter);
            } catch (DateTimeParseException ignored) {
                log.debug("Failed to parse date with parser: " + dateTimeFormatter);
            }
        }

        return null;
    }

    private LocalDate parseLocalDateCustom(LocalDate now, String date) throws DateTimeParseException {
        date = date.toLowerCase();
        Matcher matcher = pattern.matcher(date);

        if (matcher.matches()) {
            int number = Integer.parseInt(matcher.group(2));
            if (matcher.group(1).equals("-")) {
                number *= -1;
            }
            String noun = matcher.group(3);

            switch (noun) {
                case "day" -> {
                    return now.plusDays(number);
                } case "week" -> {
                    return now.plusWeeks(number);
                } case "month" -> {
                    return now.plusMonths(number);
                } case "year" -> {
                    return now.plusYears(number);
                } case "monday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.MONDAY, number));
                } case "tuesday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.TUESDAY, number));
                } case "wednesday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.WEDNESDAY, number));
                } case "thursday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.THURSDAY, number));
                } case "friday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.FRIDAY, number));
                } case "saturday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.SATURDAY, number));
                } case "sunday" -> {
                    return now.plusDays(getDaysUntilNext(now, DayOfWeek.SUNDAY, number));
                }
            }
        } else {
            switch (date.strip()) {
                case "today" -> {
                    return now;
                }
                case "tomorrow" -> {
                    return now.plusDays(1);
                }
                case "yesterday" -> {
                    return now.minusDays(1);
                }
                case "monday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.MONDAY));
                }
                case "tuesday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.TUESDAY));
                }
                case "wednesday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.WEDNESDAY));
                }
                case "thursday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.THURSDAY));
                }
                case "friday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.FRIDAY));
                }
                case "saturday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.SATURDAY));
                }
                case "sunday" -> {
                    return now.plusDays(getDaysUntil(now, DayOfWeek.SUNDAY));
                }
            }
        }
        throw new DateTimeParseException("Invalid date: " + date, date, 0);
    }

    private int getDaysUntil(LocalDate localDate, DayOfWeek dayOfWeek) {
        return (dayOfWeek.getValue()+7-localDate.getDayOfWeek().getValue())%7;
    }

    private int getDaysUntilNext(LocalDate localDate, DayOfWeek dayOfWeek, int next) {
        int days = (dayOfWeek.getValue()+7-localDate.getDayOfWeek().getValue())%7;
        int sign = (int)Math.signum(next);

        if (next < 0) {
            days = 7 - days;
        }

        if (days == 0) {
            return 7*next;
        } else {
            return sign*days + 7*(next-sign);
        }
    }


    private LocalTime parseLocalTimeStandard(String timeStr) {
        return LocalTime.parse(timeStr.toUpperCase(), timeFormatter);
    }

    private LocalTime parseLocalTimeCustom(String timeStr) {
        LocalTime now = LocalTime.now(clock);
        timeStr = timeStr.toLowerCase();
        Matcher matcher = pattern.matcher(timeStr);

        if (matcher.matches()) {
            int number = Integer.parseInt(matcher.group(2));
            if (matcher.group(1).equals("-")) {
                number *= -1;
            }
            String noun = matcher.group(3);

            switch (noun) {
                case "hour" -> {
                    return now.plusHours(number);
                } case "minute" -> {
                    return now.plusMinutes(number);
                } case "second" -> {
                    return now.plusSeconds(number);
                }
            }
        } else if (timeStr.strip().equals("now")) {
            return now;
        }
        throw new DateTimeParseException("Invalid time: " + timeStr, timeStr, 0);
    }

    private final List<DateTimeFormatterBuilder> dateFormatterBuilders;
    private final java.time.format.DateTimeFormatter timeFormatter;
    private final Pattern pattern;
    private final Clock clock;

}

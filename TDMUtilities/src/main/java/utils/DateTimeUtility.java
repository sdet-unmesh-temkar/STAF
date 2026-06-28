package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class DateTimeUtility {

    private DateTimeUtility() {}
    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";
    public static boolean isWithinTimeout(long startTime, int timeOutInSeconds) {
        return (System.currentTimeMillis() - startTime) < TimeUnit.SECONDS.toMillis(timeOutInSeconds);
    }

    public static String formatDate(String inputDate, String inputFormat, String outputFormat) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);

        LocalDate date = LocalDate.parse(inputDate,inputFormatter);
        String formattedDate = date.format(outputFormatter);//NOSONAR
        return formattedDate;
    }

    public static String datePlusDays(String inputDate,int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        LocalDate date = LocalDate.parse(inputDate, formatter);
        LocalDate updatedDate = date.plusDays(daysToAdd);

       return updatedDate.format(formatter);
    }

    public static String convertToIso(String inputDate) {
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String convertZonedDateToMyVF(String inputDate) {
        ZonedDateTime date = ZonedDateTime.parse(inputDate);
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static String currentDateTimeInFormat(String format) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(format));
    }
}

package core.common.tuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jone.sun on 2015/9/18.
 */
public class TimeUtil {
    public void getTime(){

        //java 8
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime ldt = LocalDateTime.parse("2015-09-17 20:27:00", formatter);
//
//        System.out.println("Year: " + ldt.getYear());
//        System.out.println("Month: " + ldt.getMonth().getValue());
//        System.out.println("DayOfMonth: " + ldt.getDayOfMonth());



        //joda-time
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
//        LocalDate localDate = formatter.parseLocalDate("2015-09-17 20:27:00");
//
//        System.out.println("yearOfCentury: " + localDate.getYearOfCentury());
//        System.out.println("monthOfYear: " + localDate.getMonthOfYear());
//        System.out.println("dayOfMonth: " + localDate.getDayOfMonth());

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-09-17 20:27:00");
            Calendar now = Calendar.getInstance();
            now.setTime(date);

            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1; // 0-based!
            int day = now.get(Calendar.DAY_OF_MONTH);

            System.out.println("year: " + year);
            System.out.println("month: " + month);
            System.out.println("day: " + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

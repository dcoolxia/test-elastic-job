package name.nvshen.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CronUtil {

    public static void main(String[] args) throws ParseException {
        String cron = date2Cron(new Date());
        System.out.println(cron);
        Date date = cron2Date("14 29 11 21 12 ? 2017");
        System.out.println(date);
        System.out.println(date.after(new Date()));
    }
    
    public static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }
    
    public static Date parseDateByPattern(String cron, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.parse(cron);
    }

    public static String date2Cron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }
    
    public static Date cron2Date(String cron) throws ParseException {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return parseDateByPattern(cron, dateFormat);
    }

}

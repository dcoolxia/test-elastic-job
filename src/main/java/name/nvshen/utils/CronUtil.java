package name.nvshen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CronUtil {

    public static void main(String[] args) {
        System.out.println(date2Cron(new Date()));
    }
    
    public static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String date2Cron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

}

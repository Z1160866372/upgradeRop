package com.richeninfo.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 13:51
 */
@Component
public class DateUtil {
    private static String datePattern = "yyyy-MM-dd";

    private static String dateYearPattern = "yyyy";

    private static String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

    private static String timePattern = "HH:mm";

    /**
     * Return 缺省的日期格式 (yyyy/MM/dd)
     *
     * @return 在页面中显示的日期格式
     */
    public static String getDatePattern() {
        return datePattern;
    }

    /**
     * 根据日期格式，返回日期按datePattern格式转换后的字符串
     *
     * @param aDate
     *            日期对象
     * @return 格式化后的日期的页面显示字符串
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(datePattern);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * 按照日期格式，将字符串解析为日期对象
     *
     * @param aMask
     *            输入字符串的格式
     * @param strDate
     *            一个按aMask格式排列的日期的字符串描述
     * @return Date 对象
     * @see java.text.SimpleDateFormat
     * @throws ParseException
     */
    public static final Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            // log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * This method returns the current date time in the format: yyyy/MM/dd HH:MM
     * a
     *
     * @param theTime
     *            the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(timePattern, theTime);
    }

    /**
     * This method returns the current date in the format: yyyy-MM-dd
     *
     * @return the current date
     * @throws ParseException
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(datePattern);

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time in
     * the format you specify on input
     *
     * @param aMask
     *            the date pattern the string is in
     * @param aDate
     *            a date object
     * @return a formatted string representation of the date
     *
     * @see java.text.SimpleDateFormat
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * 根据日期格式，返回日期按datePattern格式转换后的字符串
     *
     * @param aDate
     * @return
     */
    public static final String convertDateToString(Date aDate) {
        return getDateTime(datePattern, aDate);
    }


    public static final String convertDateTimeToString(Date aDate) {
        return getDateTime(dateTimePattern, aDate);
    }

    public static final String convertDateYearToString(Date aDate) {
        return getDateTime(dateYearPattern, aDate);
    }

    public static final String convertDateToString(Date aDate, String pattern) {
        return getDateTime(pattern, aDate);
    }

    /**
     * 按照日期格式，将字符串解析为日期对象
     *
     * @param strDate
     *            (格式 yyyy-MM-dd)
     * @return
     *
     * @throws ParseException
     */
    public static Date convertStringToDate(String strDate)
            throws ParseException {
        Date aDate = null;

        try {
            aDate = convertStringToDate(datePattern, strDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return aDate;
    }

    /**
     * 按照日期格式，将字符串解析为日期对象
     *
     * @param strDate
     *            (格式 yyyy-MM-dd HH:mm:ss)
     * @return
     *
     * @throws ParseException
     */
    public static Date convertStringToDateTime(String strDate)
            throws ParseException {
        Date aDate = null;

        try {
            aDate = convertStringToDate(dateTimePattern, strDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return aDate;
    }

    /**
     * 时间相加
     *
     * @param date
     * @param day
     * @return
     */
    public static Date dateAdd(Date date, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long dateDiffer(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / (1000 * 3600 * 24);
    }

    /**
     * 获取两个日期之间的天数,只精确到天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long dateDifferOfDay(Date date1, Date date2) {
        Calendar calendar=Calendar.getInstance();
        if (date1 !=null) {
            calendar.setTime(date1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Calendar calendar2=Calendar.getInstance();
        if (date2 !=null) {
            calendar2.setTime(date2);
        }
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        return  dateDiffer(calendar.getTime(), calendar2.getTime());
    }

    /**
     * 根据日期，和格式，返回对应字符串
     * @param date
     * @param format
     * @return
     */
    public static String date2String(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    /**
     * 根据指定字符串获取上个月日期字符串格式
     * @param strDate
     * @return
     */
    public static String getLastMonth(String strDate) {
        try {
            Date d = convertStringToDate(datePattern,strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MONTH, -1);

            SimpleDateFormat DateFormat = new SimpleDateFormat(datePattern);
            Date formatDate = DateFormat.parse(DateFormat.format(cal.getTime()));
            return convertDateToString(formatDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取制定日期和当前日期的月份差
     * @param pTime
     * @return
     */
    public static int checkBirthday(String pTime) throws ParseException {
        Date d = null;
        Date d1 = null;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date currTime = new java.util.Date();
        String curTime = df.format(currTime);

        try {
            d = df.parse(pTime);
            d1 = df.parse(curTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);

        c.setTime(d1);
        int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH);
        int day1 = c.get(Calendar.DATE);

        int result;
        if (year == year1) {
            result = month1 - month;// 两个日期相差几个月，即月份差
        } else {
            if(day>day1){
                result = 12 * (year1 - year) + month1 - month - 1; // 两个日期相差几个月，即月份差
            }else{
                result = 12 * (year1 - year) + month1 - month; // 两个日期相差几个月，即月份差
            }
        }
        return (int)Math.ceil(result/12);
    }

    /**
     * 长整型转换为日期类型
     * @return String 长整型对应的格式的时间
     */
    public static String long2String(long longTime,String dataFormat){
        Date d = new Date(longTime);
        SimpleDateFormat s = new SimpleDateFormat(dataFormat);
        String str = s.format(d);
        return str;
    }

    /**
     * 长整型转换为日期类型
     */
    public static Date long2Date(long longTime){
        Date d = new Date(longTime);
        return d;
    }

    public static void main(String[]args){
        System.out.println(Calendar.getInstance().getTimeInMillis());
        long longTime = 1446173857*1000L;
        System.out.println(long2String(longTime,dateTimePattern));
    }

}

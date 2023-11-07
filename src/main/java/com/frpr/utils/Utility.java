package com.frpr.utils;

import java.util.Calendar;
import java.util.Date;

public class Utility {

    public static Date setEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 900);
        return calendar.getTime();
    }
}

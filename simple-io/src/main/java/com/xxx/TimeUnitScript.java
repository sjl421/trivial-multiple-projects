package com.xxx;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by hangyudu on 2017/7/7.
 *
 */
public class TimeUnitScript {
    public static void main(String[] args) {
        long l = TimeUnit.MINUTES.toMillis(1);
        System.out.println(l);
        System.out.println(TimeUnit.DAYS.toMillis(2));
        Date date = new Date(System.currentTimeMillis());
        System.out.println(date);

        System.out.println("+++++++++++++++++++++++");
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        System.out.println(instance.get(Calendar.SECOND));
        Calendar newInstance = (Calendar) instance.clone();
        System.out.println(newInstance.get(Calendar.SECOND));
        newInstance.add(Calendar.SECOND, 10);
        System.out.println(instance.get(Calendar.SECOND));
        System.out.println(newInstance.get(Calendar.SECOND));
        Calendar c = newInstance;
        c.add(Calendar.SECOND, 10);
        System.out.println(instance.get(Calendar.SECOND));
        System.out.println(newInstance.get(Calendar.SECOND));
        System.out.println(c.get(Calendar.SECOND));
        System.out.println("+++++++++++++++++++++++");

        System.out.println(instance.toString());
        System.out.println(instance.getTime());

        System.out.println(TimeUnit.HOURS.toMillis(1));
    }
}

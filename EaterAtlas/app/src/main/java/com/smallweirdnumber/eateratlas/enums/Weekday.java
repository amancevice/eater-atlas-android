package com.smallweirdnumber.eateratlas.enums;

import java.util.Calendar;

public enum Weekday {
    SUNDAY("Sunday"),
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday");

    private String dayName;
    Weekday(String name) { dayName = name; }

    public String toString() { return dayName; }

    public static Weekday fromString(String weekday) {
        for (Weekday weekdayObj : Weekday.values()) {
            if (weekday.equals(weekdayObj.toString())) {
                return weekdayObj;
            }
        }
        return null;
    }

    public static Weekday fromCalendar(Calendar cal) {
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.SUNDAY:    { return SUNDAY; }
            case Calendar.MONDAY:    { return MONDAY; }
            case Calendar.TUESDAY:   { return TUESDAY; }
            case Calendar.WEDNESDAY: { return WEDNESDAY; }
            case Calendar.THURSDAY:  { return THURSDAY; }
            case Calendar.FRIDAY:    { return FRIDAY; }
            case Calendar.SATURDAY:  { return SATURDAY; }
            default:                 { return null; }
        }
    }

    public static Weekday getCurrent() {
        return Weekday.fromCalendar(Calendar.getInstance());
    }
}

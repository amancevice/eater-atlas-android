package com.smallweirdnumber.eateratlas.enums;

import java.util.Calendar;

public enum Meal {
    BREAKFAST("Breakfast", 5, 10),
    LUNCH("Lunch", 10, 15),
    DINNER("Dinner", 15, 20),
    LATE_NIGHT("Late Night", 20, 23);

    private String mealName;
    private int startHour, stopHour;

    Meal(String name, int start, int stop) {
        mealName = name;
        startHour = start;
        stopHour = stop;
    }

    public static Meal fromCalendar(Calendar cal) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= BREAKFAST.getStart()  && hour < BREAKFAST.getStop()) {
            return BREAKFAST;
        } else if (hour >= LUNCH.getStart() && hour < LUNCH.getStop()) {
            return LUNCH;
        } else if (hour >= DINNER.getStart() && hour < DINNER.getStop()) {
            return DINNER;
        } else {
            return LATE_NIGHT;
        }
    }

    public static Meal fromString(String meal) {
        for (Meal mealObj : Meal.values()) {
            if (meal.equals(mealObj.toString())) {
                return mealObj;
            }
        }
        return null;
    }

    public static Meal getCurrent() {
        return Meal.fromCalendar(Calendar.getInstance());
    }

    public int getStart() {
        return startHour;
    }

    public int getStop() {
        return stopHour;
    }

    public String toString() {
        return mealName;
    }
}
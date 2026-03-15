package com.example.finance;

import java.util.Calendar;

public class CalendarDay {

    private final Calendar date;
    private final boolean currentMonth;

    public CalendarDay(Calendar date, boolean currentMonth) {
        this.date = date;
        this.currentMonth = currentMonth;
    }

    public Calendar getDate() {
        return date;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }
}
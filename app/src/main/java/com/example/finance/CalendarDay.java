package com.example.finance;

import java.util.Calendar;

public class CalendarDay {

    private final Calendar date;
    private final boolean currentMonth;
    private final boolean hasTransactions;

    public CalendarDay(Calendar date, boolean currentMonth, boolean hasTransactions) {
        this.date = date;
        this.currentMonth = currentMonth;
        this.hasTransactions = hasTransactions;
    }

    public Calendar getDate() {
        return date;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public boolean hasTransactions() {
        return hasTransactions;
    }
}
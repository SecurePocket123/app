package com.example.finance.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class KalenderViewModel extends ViewModel {

    private Calendar selectedDate = Calendar.getInstance();

    public Calendar getSelectedDate() {
        return (Calendar) selectedDate.clone();
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = (Calendar) selectedDate.clone();
    }
}

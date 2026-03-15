package com.example.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class KalenderFragment extends Fragment {

    private TextView tvMonthYear;
    private TextView tvSelectedDate;
    private RecyclerView recyclerCalendar;

    private Calendar currentMonth;
    private Calendar selectedDate;
    private Calendar today;

    private CalendarAdapter calendarAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kalender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        recyclerCalendar = view.findViewById(R.id.recyclerCalendar);

        ImageButton btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = view.findViewById(R.id.btnNextMonth);
        ImageButton btnPrevYear = view.findViewById(R.id.btnPrevYear);
        ImageButton btnNextYear = view.findViewById(R.id.btnNextYear);

        today = Calendar.getInstance();
        currentMonth = Calendar.getInstance();
        selectedDate = Calendar.getInstance();

        recyclerCalendar.setLayoutManager(new GridLayoutManager(requireContext(), 7));

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        btnPrevYear.setOnClickListener(v -> {
            currentMonth.add(Calendar.YEAR, -1);
            updateCalendar();
        });

        btnNextYear.setOnClickListener(v -> {
            currentMonth.add(Calendar.YEAR, 1);
            updateCalendar();
        });

        updateCalendar();
    }

    private void updateCalendar() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
        tvMonthYear.setText(capitalize(monthFormat.format(currentMonth.getTime())));

        List<CalendarDay> days = generateCalendarDays(currentMonth);

        calendarAdapter = new CalendarAdapter(days, selectedDate, today, day -> {
            selectedDate = (Calendar) day.getDate().clone();
            SimpleDateFormat selectedFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
            tvSelectedDate.setText("Ausgewählter Tag: " + capitalize(selectedFormat.format(selectedDate.getTime())));
            updateCalendar();
        });

        recyclerCalendar.setAdapter(calendarAdapter);

        SimpleDateFormat selectedFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
        tvSelectedDate.setText("Ausgewählter Tag: " + capitalize(selectedFormat.format(selectedDate.getTime())));
    }

    private List<CalendarDay> generateCalendarDays(Calendar monthCalendar) {
        List<CalendarDay> dayList = new ArrayList<>();

        Calendar temp = (Calendar) monthCalendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
        int offset = convertToMondayFirst(firstDayOfWeek);

        for (int i = 0; i < offset; i++) {
            dayList.add(new CalendarDay(null, false));
        }

        int maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDays; day++) {
            Calendar date = (Calendar) temp.clone();
            date.set(Calendar.DAY_OF_MONTH, day);
            dayList.add(new CalendarDay(date, true));
        }

        while (dayList.size() < 42) {
            dayList.add(new CalendarDay(null, false));
        }

        return dayList;
    }

    private int convertToMondayFirst(int dayOfWeek) {
        return (dayOfWeek + 5) % 7;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase(Locale.GERMAN) + text.substring(1);
    }
}
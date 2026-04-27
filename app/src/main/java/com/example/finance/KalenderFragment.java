package com.example.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class KalenderFragment extends Fragment {

    private static final String KEY_SELECTED_DATE = "calendar_selected_date";
    private static final String KEY_CURRENT_MONTH = "calendar_current_month";

    private TextView tvMonthYear;
    private TextView tvSelectedDate;
    private TextView tvEmptyTransactions;
    private RecyclerView recyclerCalendar;
    private RecyclerView rvSelectedTransactions;

    private Calendar currentMonth;
    private Calendar selectedDate;
    private Calendar today;

    private CalendarAdapter calendarAdapter;
    private TransactionAdapter transactionAdapter;
    private AppStorage storage;

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
        tvEmptyTransactions = view.findViewById(R.id.tvEmptyTransactions);
        recyclerCalendar = view.findViewById(R.id.recyclerCalendar);
        rvSelectedTransactions = view.findViewById(R.id.rvSelectedTransactions);

        ImageButton btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = view.findViewById(R.id.btnNextMonth);
        ImageButton btnPrevYear = view.findViewById(R.id.btnPrevYear);
        ImageButton btnNextYear = view.findViewById(R.id.btnNextYear);

        storage = AppStorage.getInstance(requireContext());

        today = Calendar.getInstance();
        currentMonth = Calendar.getInstance();
        selectedDate = Calendar.getInstance();

        restoreCalendarState();

        recyclerCalendar.setLayoutManager(new GridLayoutManager(requireContext(), 7));

        transactionAdapter = new TransactionAdapter(new TransactionAdapter.OnTransactionActionListener() {
            @Override
            public void onTransactionClicked(Transaction transaction) {
                editTransaction(transaction);
            }

            @Override
            public void onTransactionLongClicked(Transaction transaction) {
                deleteTransaction(transaction);
            }
        });

        rvSelectedTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSelectedTransactions.setAdapter(transactionAdapter);

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            selectedDate = (Calendar) currentMonth.clone();
            saveCalendarState();
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            selectedDate = (Calendar) currentMonth.clone();
            saveCalendarState();
            updateCalendar();
        });

        btnPrevYear.setOnClickListener(v -> {
            currentMonth.add(Calendar.YEAR, -1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            selectedDate = (Calendar) currentMonth.clone();
            saveCalendarState();
            updateCalendar();
        });

        btnNextYear.setOnClickListener(v -> {
            currentMonth.add(Calendar.YEAR, 1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            selectedDate = (Calendar) currentMonth.clone();
            saveCalendarState();
            updateCalendar();
        });

        updateCalendar();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCalendar();
    }

    private void restoreCalendarState() {
        String savedSelectedDate = storage.getValue(KEY_SELECTED_DATE, "");
        String savedCurrentMonth = storage.getValue(KEY_CURRENT_MONTH, "");

        if (!savedSelectedDate.isEmpty()) {
            Calendar restoredSelected = parseIsoDateToCalendar(savedSelectedDate);
            if (restoredSelected != null) {
                selectedDate = restoredSelected;
            }
        }

        if (!savedCurrentMonth.isEmpty()) {
            Calendar restoredMonth = parseIsoDateToCalendar(savedCurrentMonth);
            if (restoredMonth != null) {
                currentMonth = restoredMonth;
            }
        } else {
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void saveCalendarState() {
        storage.putValue(KEY_SELECTED_DATE, calendarToIsoDate(selectedDate));
        storage.putValue(KEY_CURRENT_MONTH, calendarToIsoDate(currentMonth));
    }

    private void updateCalendar() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
        tvMonthYear.setText(capitalize(monthFormat.format(currentMonth.getTime())));

        List<CalendarDay> days = generateCalendarDays(currentMonth);

        calendarAdapter = new CalendarAdapter(days, selectedDate, today, day -> {
            selectedDate = (Calendar) day.getDate().clone();
            saveCalendarState();
            updateCalendar();
        });

        recyclerCalendar.setAdapter(calendarAdapter);

        SimpleDateFormat selectedFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
        tvSelectedDate.setText("Ausgewählter Tag: " + capitalize(selectedFormat.format(selectedDate.getTime())));

        loadSelectedDateTransactions();
    }

    private List<CalendarDay> generateCalendarDays(Calendar monthCalendar) {
        List<CalendarDay> dayList = new ArrayList<>();

        Calendar temp = (Calendar) monthCalendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        String monthStart = calendarToIsoDate(temp);

        Calendar monthEndCalendar = (Calendar) temp.clone();
        monthEndCalendar.set(Calendar.DAY_OF_MONTH, monthEndCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String monthEnd = calendarToIsoDate(monthEndCalendar);

        Set<String> transactionDates = storage.getTransactionDatesInMonth(monthStart, monthEnd);

        int firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
        int offset = convertToMondayFirst(firstDayOfWeek);

        for (int i = 0; i < offset; i++) {
            dayList.add(new CalendarDay(null, false, false));
        }

        int maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDays; day++) {
            Calendar date = (Calendar) temp.clone();
            date.set(Calendar.DAY_OF_MONTH, day);

            String isoDate = calendarToIsoDate(date);
            boolean hasTransactions = transactionDates.contains(isoDate);

            dayList.add(new CalendarDay(date, true, hasTransactions));
        }

        while (dayList.size() < 42) {
            dayList.add(new CalendarDay(null, false, false));
        }

        return dayList;
    }

    private void loadSelectedDateTransactions() {
        if (!isAdded()) return;

        String selectedIsoDate = calendarToIsoDate(selectedDate);
        List<Transaction> transactions = storage.getTransactionsForDate(selectedIsoDate);
        transactionAdapter.setTransactions(transactions);

        if (transactions.isEmpty()) {
            tvEmptyTransactions.setText("Keine Zahlungen für den ausgewählten Tag gespeichert.");
            tvEmptyTransactions.setVisibility(View.VISIBLE);
            rvSelectedTransactions.setVisibility(View.GONE);
        } else {
            tvEmptyTransactions.setVisibility(View.GONE);
            rvSelectedTransactions.setVisibility(View.VISIBLE);
        }
    }

    private void editTransaction(Transaction transaction) {
        TransactionDialogHelper.showTransactionDialog(this, transaction, updatedTransaction -> {
            storage.updateTransaction(updatedTransaction);
            updateCalendar();
            Toast.makeText(requireContext(), "Transaktion aktualisiert.", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteTransaction(Transaction transaction) {
        TransactionDialogHelper.showDeleteDialog(this, transaction, () -> {
            storage.deleteTransaction(transaction.getId());
            updateCalendar();
            Toast.makeText(requireContext(), "Transaktion gelöscht.", Toast.LENGTH_SHORT).show();
        });
    }

    private Calendar parseIsoDateToCalendar(String isoDate) {
        try {
            LocalDate localDate = LocalDate.parse(isoDate);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, localDate.getYear());
            calendar.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    private String calendarToIsoDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
    }

    private int convertToMondayFirst(int dayOfWeek) {
        return (dayOfWeek + 5) % 7;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase(Locale.GERMAN) + text.substring(1);
    }
}
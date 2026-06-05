package com.example.finance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finance.app.AppContainer;
import com.example.finance.data.model.Zahlung;
import com.example.finance.viewmodel.KalenderViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class KalenderFragment extends Fragment {

    private TextView tvMonthYear;
    private TextView tvSelectedDate;
    private TextView tvEmptySelectedDayPayments;
    private LinearLayout layoutSelectedDayPayments;
    private RecyclerView recyclerCalendar;

    private Calendar currentMonth;
    private Calendar selectedDate;
    private Calendar today;

    private CalendarAdapter calendarAdapter;
    private KalenderViewModel viewModel;

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);

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
        layoutSelectedDayPayments = view.findViewById(R.id.layoutSelectedDayPayments);
        tvEmptySelectedDayPayments = view.findViewById(R.id.tvEmptySelectedDayPayments);
        recyclerCalendar = view.findViewById(R.id.recyclerCalendar);

        ImageButton btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = view.findViewById(R.id.btnNextMonth);
        ImageButton btnPrevYear = view.findViewById(R.id.btnPrevYear);
        ImageButton btnNextYear = view.findViewById(R.id.btnNextYear);

        viewModel = new ViewModelProvider(this).get(KalenderViewModel.class);
        today = Calendar.getInstance();
        currentMonth = Calendar.getInstance();
        selectedDate = viewModel.getSelectedDate();

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

    @Override
    public void onResume() {
        super.onResume();
        refreshSelectedDayPayments();
    }

    private void updateCalendar() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
        tvMonthYear.setText(capitalize(monthFormat.format(currentMonth.getTime())));

        List<CalendarDay> days = generateCalendarDays(currentMonth);

        calendarAdapter = new CalendarAdapter(days, selectedDate, today, day -> {
            selectedDate = (Calendar) day.getDate().clone();
            viewModel.setSelectedDate(selectedDate);
            SimpleDateFormat selectedFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
            tvSelectedDate.setText("Ausgewählter Tag: " + capitalize(selectedFormat.format(selectedDate.getTime())));
            refreshSelectedDayPayments();
            updateCalendar();
        });

        recyclerCalendar.setAdapter(calendarAdapter);

        SimpleDateFormat selectedFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
        tvSelectedDate.setText("Ausgewählter Tag: " + capitalize(selectedFormat.format(selectedDate.getTime())));
        refreshSelectedDayPayments();
    }

    private void refreshSelectedDayPayments() {
        if (layoutSelectedDayPayments == null || selectedDate == null) {
            return;
        }

        layoutSelectedDayPayments.removeAllViews();
        LocalDate selectedLocalDate = toLocalDate(selectedDate);
        int count = 0;

        for (Zahlung zahlung : AppContainer.getRepository().getAlleZahlungen()) {
            if (zahlung.getDatum().equals(selectedLocalDate)) {
                layoutSelectedDayPayments.addView(createPaymentRow(zahlung));
                count++;
            }
        }

        if (count == 0) {
            layoutSelectedDayPayments.addView(tvEmptySelectedDayPayments);
        }
    }

    private View createPaymentRow(Zahlung zahlung) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dpToPx(7), 0, dpToPx(7));

        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = new TextView(requireContext());
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        title.setText(zahlung.getTitel());
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);

        TextView amount = new TextView(requireContext());
        String sign = zahlung.isEinkommen() ? "+ " : "- ";
        amount.setText(sign + currency.format(zahlung.getBetrag()));
        amount.setTextColor(zahlung.isEinkommen() ? Color.parseColor("#B8F7C1") : Color.parseColor("#FFB4B4"));
        amount.setTextSize(15);

        TextView details = new TextView(requireContext());
        details.setText("ID #" + zahlung.getId()
                + " • Kategorie: " + zahlung.getKategorieName()
                + " (#" + zahlung.getKategorieId() + ") • "
                + dateFormat.format(zahlung.getDatum()));
        details.setTextColor(Color.parseColor("#CCFFFFFF"));
        details.setTextSize(12);
        details.setPadding(0, dpToPx(3), 0, 0);

        topRow.addView(title);
        topRow.addView(amount);
        row.addView(topRow);
        row.addView(details);

        return row;
    }

    private LocalDate toLocalDate(Calendar calendar) {
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
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

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
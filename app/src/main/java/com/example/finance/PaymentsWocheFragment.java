package com.example.finance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finance.app.AppContainer;
import com.example.finance.data.model.Zahlung;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentsWocheFragment extends Fragment {

    private static final double WEEK_BUDGET = 2000.0;

    private TextView tvBudget;
    private TextView tvRest;
    private ProgressBar progressBudget;
    private LinearLayout layoutCategories;
    private LinearLayout layoutPayments;
    private TextView tvEmpty;

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM", Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_woche, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBudget = view.findViewById(R.id.tv_week_budget);
        tvRest = view.findViewById(R.id.tv_week_rest);
        progressBudget = view.findViewById(R.id.progress_week_budget);
        layoutCategories = view.findViewById(R.id.layout_week_categories);
        layoutPayments = view.findViewById(R.id.layout_week_payments);
        tvEmpty = view.findViewById(R.id.tv_empty_week_payments);

        getParentFragmentManager().setFragmentResultListener("zahlung_added", this, (requestKey, bundle) -> refreshWeek());
        refreshWeek();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshWeek();
    }

    private void refreshWeek() {
        if (layoutPayments == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Zahlung> weekExpenses = getWeekExpenses(weekStart, weekEnd);
        double spent = sumExpenses(weekExpenses);
        double rest = WEEK_BUDGET - spent;

        tvBudget.setText("Budget: " + currency.format(WEEK_BUDGET)
                + " | Ausgegeben: " + currency.format(spent));
        tvRest.setText("Rest: " + currency.format(rest));
        progressBudget.setMax((int) WEEK_BUDGET);
        progressBudget.setProgress((int) Math.min(spent, WEEK_BUDGET));

        showCategories(weekExpenses, spent);
        showPayments(weekExpenses);
    }

    private List<Zahlung> getWeekExpenses(LocalDate weekStart, LocalDate weekEnd) {
        List<Zahlung> result = new ArrayList<>();
        List<Zahlung> zahlungen = AppContainer.getRepository().getAlleZahlungen();

        for (Zahlung zahlung : zahlungen) {
            if (!zahlung.isEinkommen()
                    && !zahlung.getDatum().isBefore(weekStart)
                    && !zahlung.getDatum().isAfter(weekEnd)) {
                result.add(zahlung);
            }
        }

        return result;
    }

    private double sumExpenses(List<Zahlung> expenses) {
        double sum = 0;
        for (Zahlung zahlung : expenses) {
            sum += zahlung.getBetrag();
        }
        return sum;
    }

    private void showCategories(List<Zahlung> expenses, double total) {
        layoutCategories.removeAllViews();

        if (expenses.isEmpty()) {
            TextView empty = createSmallText("Noch keine Kategorien für diese Woche");
            layoutCategories.addView(empty);
            return;
        }

        Map<String, Double> sums = new HashMap<>();
        for (Zahlung zahlung : expenses) {
            String category = zahlung.getKategorieName();
            Double current = sums.get(category);
            if (current == null) {
                current = 0.0;
            }
            sums.put(category, current + zahlung.getBetrag());
        }

        for (String category : sums.keySet()) {
            double amount = sums.get(category);
            int percent = total <= 0 ? 0 : (int) Math.round((amount / total) * 100);
            layoutCategories.addView(createCategoryRow(category, percent, amount));
        }
    }

    private void showPayments(List<Zahlung> expenses) {
        layoutPayments.removeAllViews();

        if (expenses.isEmpty()) {
            layoutPayments.addView(tvEmpty);
            return;
        }

        for (Zahlung zahlung : expenses) {
            layoutPayments.addView(createPaymentRow(zahlung));
        }
    }

    private View createCategoryRow(String category, int percent, double amount) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dpToPx(5), 0, dpToPx(5));

        View dot = new View(requireContext());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(14), dpToPx(14));
        dotParams.setMargins(0, dpToPx(3), dpToPx(8), 0);
        dot.setLayoutParams(dotParams);
        dot.setBackgroundColor(Color.parseColor("#6FA8FF"));

        TextView text = createSmallText(category + " " + percent + "% • " + currency.format(amount));

        row.addView(dot);
        row.addView(text);
        return row;
    }

    private View createPaymentRow(Zahlung zahlung) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dpToPx(8), 0, dpToPx(8));

        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = new TextView(requireContext());
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        title.setText(dateFormat.format(zahlung.getDatum()) + "  " + zahlung.getTitel());
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);

        TextView amount = new TextView(requireContext());
        amount.setText("- " + currency.format(zahlung.getBetrag()));
        amount.setTextColor(Color.parseColor("#FFB4B4"));
        amount.setTextSize(15);

        TextView details = createSmallText("ID #" + zahlung.getId()
                + " • Kategorie: " + zahlung.getKategorieName()
                + " (#" + zahlung.getKategorieId() + ")");

        topRow.addView(title);
        topRow.addView(amount);
        row.addView(topRow);
        row.addView(details);

        return row;
    }

    private TextView createSmallText(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextColor(Color.parseColor("#CCFFFFFF"));
        textView.setTextSize(14);
        return textView;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

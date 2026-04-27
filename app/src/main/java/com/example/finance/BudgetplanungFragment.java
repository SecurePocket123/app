package com.example.finance;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class BudgetplanungFragment extends Fragment {

    private static final String KEY_MONTH_BUDGET = "month_budget";

    private EditText etMonthBudget;
    private TextView tvBudgetMonth;
    private TextView tvBudgetValue;
    private TextView tvExpensesValue;
    private TextView tvRemainingValue;

    private AppStorage storage;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budgetplanung, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnBack = view.findViewById(R.id.btn_back_to_analyse);
        etMonthBudget = view.findViewById(R.id.et_month_budget);
        tvBudgetMonth = view.findViewById(R.id.tv_budget_month);
        tvBudgetValue = view.findViewById(R.id.tv_budget_value);
        tvExpensesValue = view.findViewById(R.id.tv_expenses_value);
        tvRemainingValue = view.findViewById(R.id.tv_remaining_value);

        storage = AppStorage.getInstance(requireContext());

        btnBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_budgetplanungFragment_to_analyseFragment)
        );

        /*
         * Das Monatsbudget wird wie beim Sparziel direkt an die zentrale
         * Speicherung gebunden. Beim Laden wird der gespeicherte Wert gesetzt,
         * beim Verlassen des Feldes wird er automatisch gespeichert.
         */
        storage.bindInput(etMonthBudget, KEY_MONTH_BUDGET, "", this::updateUI);

        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Zusätzliche Sicherheit:
         * Beim Verlassen des Fragments wird das Budget nochmals gespeichert.
         */
        if (storage != null) {
            storage.saveInput(etMonthBudget, KEY_MONTH_BUDGET);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        LocalDate today = LocalDate.now();
        String monthName = today.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN);
        monthName = monthName.substring(0, 1).toUpperCase(Locale.GERMAN) + monthName.substring(1);

        tvBudgetMonth.setText("Budget für " + monthName + " " + today.getYear());

        double monthBudget = parseDouble(etMonthBudget.getText().toString());
        double monthExpenses = storage.getExpenseSumThisMonth();
        double remaining = monthBudget - monthExpenses;

        tvBudgetValue.setText(currency.format(monthBudget));
        tvExpensesValue.setText(currency.format(monthExpenses));
        tvRemainingValue.setText(currency.format(remaining));
    }

    private double parseDouble(String input) {
        if (TextUtils.isEmpty(input)) return 0;

        input = input.trim().replace(",", ".");

        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return 0;
        }
    }
}
package com.example.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.Locale;

public class SparzieleFragment extends Fragment {

    private static final String KEY_GOAL = "goal_target";
    private static final String KEY_SAVED = "goal_saved";

    private EditText etGoal, etSaved;
    private ProgressBar progressRing;
    private TextView tvCenterValue, tvCenterPercent;

    private AppStorage storage;

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sparziele, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etGoal = view.findViewById(R.id.et_goal);
        etSaved = view.findViewById(R.id.et_saved);
        progressRing = view.findViewById(R.id.progress_ring);
        tvCenterValue = view.findViewById(R.id.tv_center_value);
        tvCenterPercent = view.findViewById(R.id.tv_center_percent);

        storage = AppStorage.getInstance(requireContext());

        /*
         * Beide Felder werden direkt an die zentrale Speicherung gebunden.
         * Beim Laden werden vorhandene Werte gesetzt,
         * beim Verlassen des Feldes werden Änderungen gespeichert.
         */
        storage.bindInput(etGoal, KEY_GOAL, "", this::updateUI);
        storage.bindInput(etSaved, KEY_SAVED, "", this::updateUI);
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Zusätzliche Sicherheit:
         * Beim Verlassen des Fragments werden beide Werte nochmals gespeichert.
         */
        if (storage != null) {
            storage.saveInput(etGoal, KEY_GOAL);
            storage.saveInput(etSaved, KEY_SAVED);
        }
    }

    private void updateUI() {
        double goal = parseDouble(etGoal.getText().toString());
        double saved = parseDouble(etSaved.getText().toString());

        if (goal <= 0) {
            progressRing.setProgress(0);
            tvCenterValue.setText(currency.format(0));
            tvCenterPercent.setText("0%");
            return;
        }

        if (saved < 0) saved = 0;
        if (saved > goal) saved = goal;

        double percent = (saved / goal) * 100.0;
        int progress = (int) Math.round(percent);
        double remaining = goal - saved;

        progressRing.setProgress(progress);
        tvCenterValue.setText(currency.format(remaining));
        tvCenterPercent.setText(progress + "%");
    }

    private double parseDouble(String input) {
        if (input == null) return 0;

        input = input.trim().replace(",", ".");

        if (input.isEmpty()) return 0;

        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return 0;
        }
    }
}

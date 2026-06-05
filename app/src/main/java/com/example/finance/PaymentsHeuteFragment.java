package com.example.finance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finance.app.AppContainer;
import com.example.finance.data.model.Zahlung;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PaymentsHeuteFragment extends Fragment {

    private LinearLayout layoutPayments;
    private TextView tvEmpty;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_heute, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutPayments = view.findViewById(R.id.layout_payments_today);
        tvEmpty = view.findViewById(R.id.tv_empty_payments_today);

        getParentFragmentManager().setFragmentResultListener("zahlung_added", this, (requestKey, bundle) -> refreshPayments());
        refreshPayments();
    }

    private void refreshPayments() {
        if (layoutPayments == null) {
            return;
        }

        layoutPayments.removeAllViews();

        LocalDate heute = LocalDate.now();
        List<Zahlung> zahlungen = AppContainer.getRepository().getZahlungen(heute.getMonthValue(), heute.getYear());
        int count = 0;

        for (Zahlung zahlung : zahlungen) {
            if (zahlung.getDatum().equals(heute)) {
                layoutPayments.addView(createPaymentRow(zahlung));
                count++;
            }
        }

        if (count == 0) {
            layoutPayments.addView(tvEmpty);
        }
    }

    private View createPaymentRow(Zahlung zahlung) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dpToPx(10), 0, dpToPx(10));

        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = new TextView(requireContext());
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        title.setText(zahlung.getTitel());
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);

        TextView amount = new TextView(requireContext());
        String sign = zahlung.isEinkommen() ? "+ " : "- ";
        amount.setText(sign + currency.format(zahlung.getBetrag()));
        amount.setTextColor(Color.WHITE);
        amount.setTextSize(16);

        TextView details = new TextView(requireContext());
        details.setText("ID #" + zahlung.getId()
                + " • Kategorie: " + zahlung.getKategorieName()
                + " (#" + zahlung.getKategorieId() + ") • "
                + dateFormat.format(zahlung.getDatum()));
        details.setTextColor(Color.parseColor("#CCFFFFFF"));
        details.setTextSize(12);
        details.setPadding(0, dpToPx(4), 0, 0);

        View divider = new View(requireContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(1)
        ));
        divider.setBackgroundColor(Color.parseColor("#22FFFFFF"));

        topRow.addView(title);
        topRow.addView(amount);
        row.addView(topRow);
        row.addView(details);
        row.addView(divider);

        return row;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

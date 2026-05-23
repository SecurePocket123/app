package com.example.finance;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finance.data.model.LaufenderVertrag;
import com.example.finance.domain.calculator.GetLaufendeVertraegeUseCase;
import com.example.finance.domain.calculator.VertragsAnalyseDaten;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnalyseFragment extends Fragment {

    private TextView tvAnalyseMonth;
    private TextView tvCurrentContractTotal;
    private TextView tvContractCount;
    private TextView tvOtherContractTotal;
    private TextView tvEmptyCurrentContracts;
    private TextView tvEmptyOtherContracts;
    private LinearLayout layoutCurrentContracts;
    private LinearLayout layoutOtherContracts;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analyse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAnalyseMonth = view.findViewById(R.id.tv_analyse_month);
        tvCurrentContractTotal = view.findViewById(R.id.tv_current_contract_total);
        tvContractCount = view.findViewById(R.id.tv_contract_count);
        tvOtherContractTotal = view.findViewById(R.id.tv_other_contract_total);
        tvEmptyCurrentContracts = view.findViewById(R.id.tv_empty_current_contracts);
        tvEmptyOtherContracts = view.findViewById(R.id.tv_empty_other_contracts);
        layoutCurrentContracts = view.findViewById(R.id.layout_current_contracts);
        layoutOtherContracts = view.findViewById(R.id.layout_other_contracts);

        Button btn = view.findViewById(R.id.btn_open_budgetplanung);
        btn.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_analyseFragment_to_budgetplanungFragment)
        );

        List<LaufenderVertrag> vertraege = erstelleBeispielVertraege();
        zeigeVertraege(vertraege);
    }

    private List<LaufenderVertrag> erstelleBeispielVertraege() {
        LocalDate heute = LocalDate.now();
        LocalDate monatsStart = heute.withDayOfMonth(1);

        List<LaufenderVertrag> vertraege = new ArrayList<>();
        vertraege.add(new LaufenderVertrag("Miete", "Wohnen", 650.00, 1, monatsStart.withDayOfMonth(1)));
        vertraege.add(new LaufenderVertrag("Handyvertrag", "Kommunikation", 29.99, 1, monatsStart.withDayOfMonth(5)));
        vertraege.add(new LaufenderVertrag("Netflix", "Streaming", 12.99, 1, monatsStart.withDayOfMonth(18)));
        vertraege.add(new LaufenderVertrag("Fitnessstudio", "Freizeit", 34.90, 1, monatsStart.withDayOfMonth(25)));

        // Beispiel für eine Abbuchung, die nicht jeden Monat fällig ist.
        // Dadurch landet GEZ/Rundfunkbeitrag in einem getrennten Bereich.
        vertraege.add(new LaufenderVertrag("Rundfunkbeitrag (GEZ)", "Haushalt", 55.08, 3,
                monatsStart.plusMonths(1).withDayOfMonth(15)));

        vertraege.add(new LaufenderVertrag("Versicherung", "Absicherung", 114.00, 6,
                monatsStart.plusMonths(2).withDayOfMonth(10)));

        return vertraege;
    }

    private void zeigeVertraege(List<LaufenderVertrag> alleVertraege) {
        LocalDate heute = LocalDate.now();
        int aktuellerMonat = heute.getMonthValue();
        int aktuellesJahr = heute.getYear();

        tvAnalyseMonth.setText("Laufende Verträge für " + heute.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL, Locale.GERMANY) + " " + aktuellesJahr);

        GetLaufendeVertraegeUseCase useCase = new GetLaufendeVertraegeUseCase();
        VertragsAnalyseDaten daten = useCase.ausfuehren(alleVertraege, aktuellerMonat, aktuellesJahr);

        tvCurrentContractTotal.setText(currencyFormat.format(daten.getAktuellerGesamtbetrag()));
        tvContractCount.setText(daten.getAktuelleVertraege().size() + " Verträge in diesem Monat");
        tvOtherContractTotal.setText("Monatlicher Durchschnitt der übrigen Verträge: "
                + currencyFormat.format(daten.getSonstigerMonatsdurchschnitt()));

        layoutCurrentContracts.removeAllViews();
        layoutOtherContracts.removeAllViews();

        for (LaufenderVertrag vertrag : daten.getAktuelleVertraege()) {
            layoutCurrentContracts.addView(erstelleVertragsKarte(vertrag, true));
        }

        for (LaufenderVertrag vertrag : daten.getAndereVertraege()) {
            layoutOtherContracts.addView(erstelleVertragsKarte(vertrag, false));
        }

        tvEmptyCurrentContracts.setVisibility(daten.getAktuelleVertraege().isEmpty() ? View.VISIBLE : View.GONE);
        tvEmptyOtherContracts.setVisibility(daten.getAndereVertraege().isEmpty() ? View.VISIBLE : View.GONE);
    }

    private View erstelleVertragsKarte(LaufenderVertrag vertrag, boolean istAktuellFaellig) {
        MaterialCardView card = new MaterialCardView(requireContext());
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(1));
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(8));
        card.setLayoutParams(cardParams);

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));

        TextView title = new TextView(requireContext());
        title.setText(vertrag.getName());
        title.setTextColor(Color.parseColor("#0F172A"));
        title.setTextSize(16);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);

        TextView category = new TextView(requireContext());
        category.setText(vertrag.getKategorie() + " • " + vertrag.getIntervallText());
        category.setTextColor(Color.parseColor("#64748B"));
        category.setTextSize(13);

        TextView amount = new TextView(requireContext());
        amount.setText(currencyFormat.format(vertrag.getBetrag()));
        amount.setTextColor(Color.parseColor("#EF4444"));
        amount.setTextSize(18);
        amount.setTypeface(amount.getTypeface(), Typeface.BOLD);
        amount.setPadding(0, dpToPx(8), 0, 0);

        TextView date = new TextView(requireContext());
        date.setText("Abbuchung: " + dateFormat.format(vertrag.getNaechsteAbbuchung()));
        date.setTextColor(Color.parseColor("#64748B"));
        date.setTextSize(13);
        date.setPadding(0, dpToPx(4), 0, 0);

        TextView status = new TextView(requireContext());
        if (istAktuellFaellig) {
            status.setText("Status: fällig in diesem Monat");
        } else {
            status.setText("Status: nicht in diesem Monat • Ø "
                    + currencyFormat.format(vertrag.getMonatlicherDurchschnitt()) + " pro Monat");
        }
        status.setTextColor(Color.parseColor("#64748B"));
        status.setTextSize(13);
        status.setPadding(0, dpToPx(4), 0, 0);

        content.addView(title);
        content.addView(category);
        content.addView(amount);
        content.addView(date);
        content.addView(status);
        card.addView(content);

        return card;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
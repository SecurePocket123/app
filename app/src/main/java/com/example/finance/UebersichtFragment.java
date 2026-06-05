package com.example.finance;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.finance.app.AppContainer;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.repository.FinanceRepository;
import com.example.finance.domain.calculator.AddAusgabenUseCase;
import com.example.finance.domain.calculator.AddEinkommenUseCase;
import com.example.finance.domain.calculator.AddKategorieUseCase;
import com.example.finance.domain.calculator.DashboardDaten;
import com.example.finance.viewmodel.UebersichtViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UebersichtFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UebersichtFragment() {
    }

    public static UebersichtFragment newInstance(String param1, String param2) {
        UebersichtFragment fragment = new UebersichtFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uebersicht, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateDashboard(view);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> showAddPaymentDialog(view));

        TabLayout tabs = view.findViewById(R.id.tabs_period);
        ViewPager2 pager = view.findViewById(R.id.pager_period);

        pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public androidx.fragment.app.Fragment createFragment(int position) {
                if (position == 0) return new PaymentsHeuteFragment();
                if (position == 1) return new PaymentsWocheFragment();
                return new PaymentsMonatFragment();
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            if (position == 0) tab.setText("Heute");
            else if (position == 1) tab.setText("Diese Woche");
            else tab.setText("Dieser Monat");
        }).attach();
    }

    private void updateDashboard(View view) {
        UebersichtViewModel viewModel = new ViewModelProvider(this).get(UebersichtViewModel.class);
        DashboardDaten daten = viewModel.getDashboardDaten();
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        TextView balanceTitle = view.findViewById(R.id.tv_balance_title);
        TextView balance = view.findViewById(R.id.tv_balance_value);
        TextView lastTxTitle = view.findViewById(R.id.tv_last_tx_title);
        TextView lastTx = view.findViewById(R.id.tv_last_tx_value);

        balanceTitle.setText("Aktuelle Bilanz");
        balance.setText(currency.format(daten.getKontostand()));
        lastTxTitle.setText("Ausgaben");
        lastTx.setText("- " + currency.format(daten.getGesamtausgaben()));
    }

    private void showAddPaymentDialog(View rootView) {
        FinanceRepository repository = AppContainer.getRepository();
        ensureDefaultCategories(repository);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(20);
        layout.setPadding(padding, dpToPx(8), padding, 0);

        RadioGroup typeGroup = new RadioGroup(requireContext());
        typeGroup.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rbAusgabe = new RadioButton(requireContext());
        rbAusgabe.setText("Ausgabe");
        rbAusgabe.setId(View.generateViewId());

        RadioButton rbEinnahme = new RadioButton(requireContext());
        rbEinnahme.setText("Einnahme");
        rbEinnahme.setId(View.generateViewId());

        typeGroup.addView(rbAusgabe);
        typeGroup.addView(rbEinnahme);
        typeGroup.check(rbAusgabe.getId());

        EditText etBetrag = new EditText(requireContext());
        etBetrag.setHint("Betrag, z.B. 12,50");
        etBetrag.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText etBeschreibung = new EditText(requireContext());
        etBeschreibung.setHint("Beschreibung, z.B. Rewe");

        Spinner spinnerKategorie = new Spinner(requireContext());
        List<Kategorie> kategorien = repository.getKategorien();
        List<String> kategorieNamen = new ArrayList<>();
        for (Kategorie kategorie : kategorien) {
            kategorieNamen.add(kategorie.getName() + " (#" + kategorie.getId() + ")");
        }
        spinnerKategorie.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                kategorieNamen
        ));

        EditText etZahlungsart = new EditText(requireContext());
        etZahlungsart.setHint("Zahlungsart, z.B. Karte");

        layout.addView(typeGroup);
        layout.addView(etBetrag);
        layout.addView(etBeschreibung);
        layout.addView(spinnerKategorie);
        layout.addView(etZahlungsart);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Zahlung hinzufügen")
                .setView(layout)
                .setNegativeButton("Abbrechen", null)
                .setPositiveButton("Speichern", null)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            boolean saved = savePayment(
                    rootView,
                    repository,
                    kategorien,
                    spinnerKategorie.getSelectedItemPosition(),
                    typeGroup.getCheckedRadioButtonId() == rbEinnahme.getId(),
                    etBetrag.getText().toString(),
                    etBeschreibung.getText().toString(),
                    etZahlungsart.getText().toString()
            );

            if (saved) {
                dialog.dismiss();
            }
        });
    }

    private boolean savePayment(View rootView, FinanceRepository repository, List<Kategorie> kategorien,
                                int selectedKategorieIndex, boolean isEinnahme, String betragText,
                                String beschreibung, String zahlungsart) {
        double betrag = parseBetrag(betragText);

        if (betrag <= 0) {
            Toast.makeText(requireContext(), "Bitte gültigen Betrag eingeben", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (beschreibung == null || beschreibung.trim().isEmpty()) {
            beschreibung = isEinnahme ? "Einnahme" : "Ausgabe";
        }

        if (zahlungsart == null || zahlungsart.trim().isEmpty()) {
            zahlungsart = "Unbekannt";
        }

        Kategorie kategorie = kategorien.get(selectedKategorieIndex);

        if (isEinnahme) {
            new AddEinkommenUseCase(repository).ausfuehren(
                    betrag,
                    kategorie.getId(),
                    LocalDate.now(),
                    beschreibung.trim()
            );
        } else {
            new AddAusgabenUseCase(repository).ausfuehren(
                    betrag,
                    kategorie.getId(),
                    LocalDate.now(),
                    beschreibung.trim(),
                    zahlungsart.trim()
            );
        }

        updateDashboard(rootView);
        getChildFragmentManager().setFragmentResult("zahlung_added", new Bundle());
        Toast.makeText(requireContext(), "Zahlung gespeichert", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void ensureDefaultCategories(FinanceRepository repository) {
        if (!repository.getKategorien().isEmpty()) {
            return;
        }

        AddKategorieUseCase addKategorie = new AddKategorieUseCase(repository);
        addKategorie.ausfuehren("Allgemein", "#FFFFFF");
        addKategorie.ausfuehren("Essen", "#FF0000");
        addKategorie.ausfuehren("Gehalt", "#00FF00");
    }

    private double parseBetrag(String input) {
        if (input == null) {
            return 0;
        }

        input = input.trim().replace(",", ".");
        if (input.isEmpty()) {
            return 0;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}

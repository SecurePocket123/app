package com.example.finance;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.finance.domain.calculator.DashboardDaten;
import com.example.finance.viewmodel.UebersichtViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
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
}
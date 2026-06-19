package com.example.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.NumberFormat;
import java.util.Locale;

public class UebersichtFragment extends Fragment {

    private TextView tvBalanceValue;
    private TextView tvLastTxValue;
    private View fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uebersicht, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBalanceValue = view.findViewById(R.id.tv_balance_value);
        tvLastTxValue = view.findViewById(R.id.tv_last_tx_value);
        fabAdd = view.findViewById(R.id.fab_add);

        setupTabs(view);
        refreshSummary();

        fabAdd.setOnClickListener(v -> showAddTransactionDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSummary();
    }

    private void setupTabs(@NonNull View view) {
        TabLayout tabs = view.findViewById(R.id.tabs_period);
        ViewPager2 pager = view.findViewById(R.id.pager_period);

        pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
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

    private void showAddTransactionDialog() {
        TransactionDialogHelper.showTransactionDialog(this, null, transaction -> {
            AppStorage.getInstance(requireContext()).addTransaction(transaction);
            notifyTransactionsChanged();
            Toast.makeText(requireContext(), "Transaktion gespeichert.", Toast.LENGTH_SHORT).show();
        });
    }

    /*
     * Öffentliche Methode für Unterfragmente.
     * Damit kann die Summary Card nach Bearbeiten oder Löschen aktualisiert werden.
     */
    public void refreshSummaryFromChild() {
        refreshSummary();
    }

    private void notifyTransactionsChanged() {
        refreshSummary();
        getChildFragmentManager().setFragmentResult("transactions_changed", new Bundle());
    }

    private void refreshSummary() {
        if (!isAdded() || getContext() == null) {
            return;
        }

        AppStorage storage = AppStorage.getInstance(requireContext());
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        double balance = storage.getBalance();
        tvBalanceValue.setText(currency.format(balance));

        Transaction lastTransaction = storage.getLastTransaction();
        if (lastTransaction != null) {
            tvLastTxValue.setText(lastTransaction.getFormattedAmount());
        } else {
            tvLastTxValue.setText("—");
        }
    }
}
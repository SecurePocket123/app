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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentsHeuteFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TextView tvEmpty;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_heute, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTransactions = view.findViewById(R.id.rv_transactions);
        tvEmpty = view.findViewById(R.id.tv_empty_transactions);

        adapter = new TransactionAdapter(new TransactionAdapter.OnTransactionActionListener() {
            @Override
            public void onTransactionClicked(Transaction transaction) {
                editTransaction(transaction);
            }

            @Override
            public void onTransactionLongClicked(Transaction transaction) {
                deleteTransaction(transaction);
            }
        });

        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener(
                "transactions_changed",
                getViewLifecycleOwner(),
                (requestKey, result) -> loadTransactions()
        );

        loadTransactions();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void editTransaction(Transaction transaction) {
        TransactionDialogHelper.showTransactionDialog(this, transaction, updatedTransaction -> {
            AppStorage.getInstance(requireContext()).updateTransaction(updatedTransaction);
            notifyTransactionChange("Transaktion aktualisiert.");
        });
    }

    private void deleteTransaction(Transaction transaction) {
        TransactionDialogHelper.showDeleteDialog(this, transaction, () -> {
            AppStorage.getInstance(requireContext()).deleteTransaction(transaction.getId());
            notifyTransactionChange("Transaktion gelöscht.");
        });
    }

    private void notifyTransactionChange(String message) {
        loadTransactions();

        if (getParentFragment() instanceof UebersichtFragment) {
            ((UebersichtFragment) getParentFragment()).refreshSummaryFromChild();
        }

        getParentFragmentManager().setFragmentResult("transactions_changed", new Bundle());
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadTransactions() {
        if (!isAdded()) return;

        List<Transaction> transactions = AppStorage.getInstance(requireContext()).getTransactionsToday();
        adapter.setTransactions(transactions);

        if (transactions.isEmpty()) {
            tvEmpty.setText("Keine Zahlungen für heute gespeichert.");
            tvEmpty.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }
}
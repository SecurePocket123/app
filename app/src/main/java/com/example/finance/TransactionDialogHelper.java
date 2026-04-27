package com.example.finance;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
 * Hilfsklasse für Dialoge rund um Transaktionen.
 *
 * Diese Klasse kapselt:
 * - Dialog zum Anlegen
 * - Dialog zum Bearbeiten
 * - Dialog zum Löschen
 *
 * Dadurch muss dieselbe Dialoglogik nicht in mehreren Fragments wiederholt werden.
 */
public final class TransactionDialogHelper {

    private TransactionDialogHelper() {
    }

    public interface OnTransactionSavedListener {
        void onTransactionSaved(Transaction transaction);
    }

    public static void showTransactionDialog(
            @NonNull Fragment fragment,
            @Nullable Transaction existingTransaction,
            @NonNull OnTransactionSavedListener listener
    ) {
        if (!fragment.isAdded()) return;

        View dialogView = LayoutInflater.from(fragment.requireContext())
                .inflate(R.layout.dialog_add_transaction, null, false);

        EditText etTitle = dialogView.findViewById(R.id.et_tx_title);
        EditText etDate = dialogView.findViewById(R.id.et_tx_date);
        EditText etAmount = dialogView.findViewById(R.id.et_tx_amount);
        CheckBox cbIsIncome = dialogView.findViewById(R.id.cb_is_income);

        boolean isEdit = existingTransaction != null;

        if (isEdit) {
            etTitle.setText(existingTransaction.getTitle());
            etDate.setText(existingTransaction.getDate());
            etAmount.setText(String.valueOf(existingTransaction.getAmount()));
            cbIsIncome.setChecked(existingTransaction.isIncome());
        } else {
            etDate.setText(LocalDate.now().toString());
        }

        AlertDialog dialog = new AlertDialog.Builder(fragment.requireContext())
                .setTitle(isEdit ? "Transaktion bearbeiten" : "Transaktion hinzufügen")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Aktualisieren" : "Speichern", null)
                .setNegativeButton("Abbrechen", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String amountText = etAmount.getText().toString().trim().replace(",", ".");
            boolean isIncome = cbIsIncome.isChecked();

            if (title.isEmpty()) {
                Toast.makeText(fragment.requireContext(), "Bitte einen Titel eingeben.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (date.isEmpty()) {
                Toast.makeText(fragment.requireContext(), "Bitte ein Datum eingeben.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                Toast.makeText(fragment.requireContext(), "Datum muss im Format yyyy-MM-dd eingegeben werden.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (Exception e) {
                Toast.makeText(fragment.requireContext(), "Bitte einen gültigen Betrag eingeben.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(fragment.requireContext(), "Der Betrag muss größer als 0 sein.", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = isEdit ? existingTransaction.getId() : -1;
            int iconRes = isEdit ? existingTransaction.getIconResource() : R.drawable.ic_add;

            Transaction transaction = new Transaction(
                    id,
                    title,
                    date,
                    amount,
                    isIncome,
                    iconRes
            );

            listener.onTransactionSaved(transaction);
            dialog.dismiss();
        }));

        dialog.show();
    }

    public static void showDeleteDialog(
            @NonNull Fragment fragment,
            @NonNull Transaction transaction,
            @NonNull Runnable onDeleteConfirmed
    ) {
        if (!fragment.isAdded()) return;

        new AlertDialog.Builder(fragment.requireContext())
                .setTitle("Transaktion löschen")
                .setMessage("Soll die Transaktion \"" + transaction.getTitle() + "\" gelöscht werden?")
                .setPositiveButton("Löschen", (dialog, which) -> onDeleteConfirmed.run())
                .setNegativeButton("Abbrechen", null)
                .show();
    }
}
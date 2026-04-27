package com.example.finance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Zentrale Speicherklasse der App.
 *
 * Diese Klasse enthält:
 * 1. allgemeine Schlüssel/Wert-Speicherung für einfache Einzelwerte
 * 2. eine Tabelle für Transaktionen
 * 3. Methoden für Bilanz, letzte Transaktion und Zeitfilter
 * 4. Methoden für Bearbeiten, Löschen und Kalender-Verknüpfung
 * 5. Methoden für Budgetplanung auf Basis gespeicherter Transaktionen
 */
public class AppStorage extends SQLiteOpenHelper {

    private static final String DB_NAME = "finance.db";
    private static final int DB_VERSION = 1;

    private static AppStorage instance;

    public static synchronized AppStorage getInstance(Context context) {
        if (instance == null) {
            instance = new AppStorage(context.getApplicationContext());
        }
        return instance;
    }

    private AppStorage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE app_values (" +
                        "storage_key TEXT PRIMARY KEY, " +
                        "storage_value TEXT NOT NULL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE transactions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "date TEXT NOT NULL, " +
                        "amount REAL NOT NULL, " +
                        "is_income INTEGER NOT NULL, " +
                        "icon_resource INTEGER NOT NULL DEFAULT 0" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // vorerst leer
    }

    // -------------------------------------------------
    // Bereich A: einfache Schlüssel/Wert-Speicherung
    // -------------------------------------------------

    public void putValue(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("storage_key", key);
        values.put("storage_value", value == null ? "" : value);

        db.insertWithOnConflict(
                "app_values",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public String getValue(String key, String defaultValue) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT storage_value FROM app_values WHERE storage_key = ?",
                new String[]{key}
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return defaultValue == null ? "" : defaultValue;
        } finally {
            cursor.close();
        }
    }

    public void bindInput(EditText input, String key, String defaultValue, Runnable onUiChanged) {
        input.setText(getValue(key, defaultValue));

        if (onUiChanged != null) {
            onUiChanged.run();
        }

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                putValue(key, input.getText().toString().trim());

                if (onUiChanged != null) {
                    onUiChanged.run();
                }
            }
        });
    }

    public void saveInput(EditText input, String key) {
        putValue(key, input.getText().toString().trim());
    }

    // -------------------------------------------------
    // Bereich B: Transaktionen
    // -------------------------------------------------

    public long addTransaction(Transaction transaction) {
        if (transaction == null) return -1;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", transaction.getTitle());
        values.put("date", transaction.getDate());
        values.put("amount", transaction.getAmount());
        values.put("is_income", transaction.isIncome() ? 1 : 0);
        values.put("icon_resource", transaction.getIconResource());

        return db.insert("transactions", null, values);
    }

    public int updateTransaction(Transaction transaction) {
        if (transaction == null || transaction.getId() < 0) return 0;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", transaction.getTitle());
        values.put("date", transaction.getDate());
        values.put("amount", transaction.getAmount());
        values.put("is_income", transaction.isIncome() ? 1 : 0);
        values.put("icon_resource", transaction.getIconResource());

        return db.update(
                "transactions",
                values,
                "id = ?",
                new String[]{String.valueOf(transaction.getId())}
        );
    }

    public int deleteTransaction(long id) {
        if (id < 0) return 0;

        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                "transactions",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public List<Transaction> getTransactions() {
        return getTransactionsByRange(null, null);
    }

    public List<Transaction> getTransactionsToday() {
        String today = LocalDate.now().toString();
        return getTransactionsByRange(today, today);
    }

    public List<Transaction> getTransactionsThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return getTransactionsByRange(start.toString(), end.toString());
    }

    public List<Transaction> getTransactionsThisMonth() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = today.with(TemporalAdjusters.lastDayOfMonth());
        return getTransactionsByRange(start.toString(), end.toString());
    }

    public List<Transaction> getTransactionsForDate(String date) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, title, date, amount, is_income, icon_resource " +
                        "FROM transactions WHERE date = ? " +
                        "ORDER BY id DESC",
                new String[]{date}
        );

        try {
            while (cursor.moveToNext()) {
                transactions.add(readTransaction(cursor));
            }
        } finally {
            cursor.close();
        }

        return transactions;
    }

    public Set<String> getTransactionDatesInMonth(String monthStart, String monthEnd) {
        Set<String> dates = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT date FROM transactions " +
                        "WHERE date >= ? AND date <= ?",
                new String[]{monthStart, monthEnd}
        );

        try {
            while (cursor.moveToNext()) {
                dates.add(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }

        return dates;
    }

    public List<Transaction> getTransactionsByRange(@Nullable String startDate, @Nullable String endDate) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;

        if (startDate == null || endDate == null) {
            cursor = db.rawQuery(
                    "SELECT id, title, date, amount, is_income, icon_resource " +
                            "FROM transactions ORDER BY date DESC, id DESC",
                    null
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT id, title, date, amount, is_income, icon_resource " +
                            "FROM transactions " +
                            "WHERE date >= ? AND date <= ? " +
                            "ORDER BY date DESC, id DESC",
                    new String[]{startDate, endDate}
            );
        }

        try {
            while (cursor.moveToNext()) {
                transactions.add(readTransaction(cursor));
            }
        } finally {
            cursor.close();
        }

        return transactions;
    }

    @Nullable
    public Transaction getLastTransaction() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, title, date, amount, is_income, icon_resource " +
                        "FROM transactions ORDER BY id DESC LIMIT 1",
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return readTransaction(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public double getBalance() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(CASE WHEN is_income = 1 THEN amount ELSE -amount END), 0) " +
                        "FROM transactions",
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }

    /*
     * Summe aller Ausgaben des aktuellen Monats.
     * Es werden nur Transaktionen mit is_income = 0 berücksichtigt.
     */
    public double getExpenseSumThisMonth() {
        LocalDate today = LocalDate.now();
        String start = today.withDayOfMonth(1).toString();
        String end = today.with(TemporalAdjusters.lastDayOfMonth()).toString();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE is_income = 0 AND date >= ? AND date <= ?",
                new String[]{start, end}
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }

    /*
     * Summe aller Einnahmen des aktuellen Monats.
     * Diese Methode ist optional für spätere Erweiterungen nützlich.
     */
    public double getIncomeSumThisMonth() {
        LocalDate today = LocalDate.now();
        String start = today.withDayOfMonth(1).toString();
        String end = today.with(TemporalAdjusters.lastDayOfMonth()).toString();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE is_income = 1 AND date >= ? AND date <= ?",
                new String[]{start, end}
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("transactions", null, null);
    }

    private Transaction readTransaction(Cursor cursor) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String date = cursor.getString(2);
        double amount = cursor.getDouble(3);
        boolean isIncome = cursor.getInt(4) == 1;
        int iconResource = cursor.getInt(5);

        return new Transaction(id, title, date, amount, isIncome, iconResource);
    }
}
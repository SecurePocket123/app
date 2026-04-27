package com.example.finance;

import java.util.Locale;

public class Transaction {

    private long id;
    private String title;
    private String date;
    private double amount;
    private boolean isIncome;
    private int iconResource;

    /*
     * Bestehender Konstruktor bleibt erhalten,
     * damit vorhandener Code kompatibel bleibt.
     */
    public Transaction(String title, String date, double amount, boolean isIncome, int iconResource) {
        this(-1, title, date, amount, isIncome, iconResource);
    }

    /*
     * Zusätzlicher Konstruktor mit Datenbank-id.
     * Dieser wird von AppStorage beim Laden aus SQLite verwendet.
     */
    public Transaction(long id, String title, String date, double amount, boolean isIncome, int iconResource) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isIncome = isIncome;
        this.iconResource = iconResource;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getFormattedAmount() {
        String sign = isIncome ? "+ " : "- ";
        return sign + "€ " + String.format(Locale.GERMANY, "%.2f", Math.abs(amount));
    }
}

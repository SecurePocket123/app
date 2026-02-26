package com.example.finance;

public class Transaction {
    private String title;
    private String date;
    private double amount;
    private boolean isIncome;
    private int iconResource;

    public Transaction(String title, String date, double amount, boolean isIncome, int iconResource) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isIncome = isIncome;
        this.iconResource = iconResource;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public boolean isIncome() { return isIncome; }
    public int getIconResource() { return iconResource; }
    public String getFormattedAmount() {
        String sign = isIncome ? "+ " : "- ";
        return sign + "€ " + String.format("%.2f", Math.abs(amount));
    }
}

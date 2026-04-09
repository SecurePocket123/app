package com.example.finance.data.model;

public class Kategorie {

    private long id;
    private String name;
    private String farbe; // fürs Ui

    public Kategorie(String name, String farbe) {
        this.name = name;
        this.farbe = farbe;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFarbe() {
        return farbe;
    }
}
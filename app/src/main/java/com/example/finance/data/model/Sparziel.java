package com.example.finance.data.model;

import java.time.LocalDate;

public class Sparziel {

    private long id;
    private String name;
    private double zielbetrag;
    private double aktuellerBetrag;
    private LocalDate zieldatum;

    public Sparziel(String name, double zielbetrag, LocalDate zieldatum) {
        this.name = name;
        this.zielbetrag = zielbetrag;
        this.zieldatum = zieldatum;
        this.aktuellerBetrag = 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getZielbetrag() {
        return zielbetrag;
    }

    public double getAktuellerBetrag() {
        return aktuellerBetrag;
    }

    public LocalDate getZieldatum() {
        return zieldatum;
    }

    public double getFortschrittInProzent() {
        if (zielbetrag == 0) {
            return 0;
        }
        return (aktuellerBetrag / zielbetrag) * 100;
    }

    public void setAktuellerBetrag(double aktuellerBetrag) {
        this.aktuellerBetrag = aktuellerBetrag;
    }
}
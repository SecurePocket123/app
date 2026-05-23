package com.example.finance.data.model;

import java.time.LocalDate;

/**
 * Modellklasse für wiederkehrende Verträge oder Abbuchungen.
 * Beispiel: Miete monatlich, Handyvertrag monatlich, GEZ alle 3 Monate.
 */
public class LaufenderVertrag {

    private String name;
    private String kategorie;
    private double betrag;
    private int intervallInMonaten;
    private LocalDate naechsteAbbuchung;

    public LaufenderVertrag(String name, String kategorie, double betrag,
                            int intervallInMonaten, LocalDate naechsteAbbuchung) {
        this.name = name;
        this.kategorie = kategorie;
        this.betrag = betrag;
        this.intervallInMonaten = intervallInMonaten;
        this.naechsteAbbuchung = naechsteAbbuchung;
    }

    public String getName() {
        return name;
    }

    public String getKategorie() {
        return kategorie;
    }

    public double getBetrag() {
        return betrag;
    }

    public int getIntervallInMonaten() {
        return intervallInMonaten;
    }

    public LocalDate getNaechsteAbbuchung() {
        return naechsteAbbuchung;
    }

    public boolean istFaelligInMonat(int monat, int jahr) {
        return naechsteAbbuchung.getMonthValue() == monat
                && naechsteAbbuchung.getYear() == jahr;
    }

    public double getMonatlicherDurchschnitt() {
        if (intervallInMonaten <= 0) {
            return 0;
        }
        return betrag / intervallInMonaten;
    }

    public String getIntervallText() {
        if (intervallInMonaten == 1) {
            return "monatlich";
        }
        return "alle " + intervallInMonaten + " Monate";
    }
}
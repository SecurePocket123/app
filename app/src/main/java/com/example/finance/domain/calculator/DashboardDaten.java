package com.example.finance.domain.calculator;

/**
 * Diese Klasse hält die berechneten Dashboard-Werte.
 */
public class DashboardDaten {

    private double kontostand;
    private double gesamteinnahmen;
    private double gesamtausgaben;
    private int anzahlSparziele;

    public DashboardDaten(double kontostand, double gesamteinnahmen,
                          double gesamtausgaben, int anzahlSparziele) {
        this.kontostand = kontostand;
        this.gesamteinnahmen = gesamteinnahmen;
        this.gesamtausgaben = gesamtausgaben;
        this.anzahlSparziele = anzahlSparziele;
    }

    public double getKontostand() {
        return kontostand;
    }

    public double getGesamteinnahmen() {
        return gesamteinnahmen;
    }

    public double getGesamtausgaben() {
        return gesamtausgaben;
    }

    public int getAnzahlSparziele() {
        return anzahlSparziele;
    }
}
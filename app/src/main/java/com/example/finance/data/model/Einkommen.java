package com.example.finance.data.model;

import java.time.LocalDate;

public class Einkommen {
    private long id; // gibt dir eine eindeutige ID (maybe für Datenbank)
    private double betrag;
    private long kategorieId;  //Verknüpfung zur Kategorie
    private LocalDate datum; //  Datum der Einnahme (https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)
    private String beschreibung; // // optionale Beschreibung der transaktion
    //Konstruktoren + Getter/setter

    public Einkommen (double betrag, long kategorieId, LocalDate datum, String beschreibung) {
        this.betrag = betrag;
        this.kategorieId = kategorieId;
        this.datum = datum;
        this.beschreibung = beschreibung;
    }

    // Getter = Werte lesen

    public long getId() {
        return id;
    }

    public double getBetrag() {
        return betrag;
    }

    public long getKategorieId() {
        return kategorieId;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    // setter noch nicht gesetzt weil keine Ahnung wofür (maybe für testwerte aber dann eigentlich per Datenbank eingabe testen)
}

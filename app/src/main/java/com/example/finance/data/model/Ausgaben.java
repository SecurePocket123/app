package com.example.finance.data.model;

import java.time.LocalDate;

public class Ausgaben { // Kommentare identisch zu Klasse Einkommen bis auf Paymenttype
    private long id;
    private double betrag;
    private long kategorieId;
    private LocalDate datum;
    private String beschreibung;
    private String zahlungsart; // z.B. Bar Karte, Paypal


    public Ausgaben(double betrag, long kategorieId, LocalDate datum,
                    String beschreibung, String zahlungsart) {
        this.betrag = betrag;
        this.kategorieId = kategorieId;
        this.datum = datum;
        this.beschreibung = beschreibung;
        this.zahlungsart = zahlungsart;
    }
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
    public String getZahlungsart() {
        return zahlungsart;
    }
}



package com.example.finance.data.model;

import java.time.LocalDate;

public class Zahlung {

    private long id;
    private long kategorieId;
    private String kategorieName;
    private String titel;
    private LocalDate datum;
    private double betrag;
    private boolean einkommen;

    public Zahlung(long id, long kategorieId, String kategorieName, String titel,
                   LocalDate datum, double betrag, boolean einkommen) {
        this.id = id;
        this.kategorieId = kategorieId;
        this.kategorieName = kategorieName;
        this.titel = titel;
        this.datum = datum;
        this.betrag = betrag;
        this.einkommen = einkommen;
    }

    public long getId() {
        return id;
    }

    public long getKategorieId() {
        return kategorieId;
    }

    public String getKategorieName() {
        return kategorieName;
    }

    public String getTitel() {
        return titel;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public double getBetrag() {
        return betrag;
    }

    public boolean isEinkommen() {
        return einkommen;
    }
}

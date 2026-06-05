package com.example.finance.data.storage;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;

import java.util.ArrayList;
import java.util.List;

public class FinanceSaveData {

    private long nextPaymentId = 1;
    private long nextKategorieId = 1;
    private List<Einkommen> einkommenListe = new ArrayList<>();
    private List<Ausgaben> ausgabenListe = new ArrayList<>();
    private List<Kategorie> kategorienListe = new ArrayList<>();
    private List<Sparziel> sparzieleListe = new ArrayList<>();

    public long getNextPaymentId() {
        return nextPaymentId;
    }

    public void setNextPaymentId(long nextPaymentId) {
        this.nextPaymentId = nextPaymentId;
    }

    public long getNextKategorieId() {
        return nextKategorieId;
    }

    public void setNextKategorieId(long nextKategorieId) {
        this.nextKategorieId = nextKategorieId;
    }

    public List<Einkommen> getEinkommenListe() {
        return einkommenListe;
    }

    public void setEinkommenListe(List<Einkommen> einkommenListe) {
        this.einkommenListe = einkommenListe;
    }

    public List<Ausgaben> getAusgabenListe() {
        return ausgabenListe;
    }

    public void setAusgabenListe(List<Ausgaben> ausgabenListe) {
        this.ausgabenListe = ausgabenListe;
    }

    public List<Kategorie> getKategorienListe() {
        return kategorienListe;
    }

    public void setKategorienListe(List<Kategorie> kategorienListe) {
        this.kategorienListe = kategorienListe;
    }

    public List<Sparziel> getSparzieleListe() {
        return sparzieleListe;
    }

    public void setSparzieleListe(List<Sparziel> sparzieleListe) {
        this.sparzieleListe = sparzieleListe;
    }
}

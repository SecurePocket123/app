package com.example.finance.data.repository;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;

import java.util.ArrayList;
import java.util.List;

/**
 * Einfache In-Memory-Implementierung.
 * Die Daten bleiben nur solange die App läuft.
 */
public class FinanceRepositoryImpl implements FinanceRepository {

    private List<Einkommen> einkommenListe = new ArrayList<>();
    private List<Ausgaben> ausgabenListe = new ArrayList<>();
    private List<Kategorie> kategorienListe = new ArrayList<>();
    private List<Sparziel> sparzieleListe = new ArrayList<>();

    @Override
    public void addEinkommen(Einkommen einkommen) {
        einkommenListe.add(einkommen);
    }

    @Override
    public void addAusgaben(Ausgaben ausgaben) {
        ausgabenListe.add(ausgaben);
    }

    @Override
    public void addKategorie(Kategorie kategorie) {
        kategorienListe.add(kategorie);
    }

    @Override
    public void addSparziel(Sparziel sparziel) {
        sparzieleListe.add(sparziel);
    }

    @Override
    public List<Einkommen> getEinkommen(int monat, int jahr) {
        List<Einkommen> gefilterteEinkommen = new ArrayList<>();

        for (Einkommen einkommen : einkommenListe) {
            if (einkommen.getDatum().getMonthValue() == monat
                    && einkommen.getDatum().getYear() == jahr) {
                gefilterteEinkommen.add(einkommen);
            }
        }

        return gefilterteEinkommen;
    }

    @Override
    public List<Ausgaben> getAusgaben(int monat, int jahr) {
        List<Ausgaben> gefilterteAusgaben = new ArrayList<>();

        for (Ausgaben ausgabe : ausgabenListe) {
            if (ausgabe.getDatum().getMonthValue() == monat
                    && ausgabe.getDatum().getYear() == jahr) {
                gefilterteAusgaben.add(ausgabe);
            }
        }

        return gefilterteAusgaben;
    }

    @Override
    public List<Kategorie> getKategorien() {
        return kategorienListe;
    }

    @Override
    public List<Sparziel> getSparziele() {
        return sparzieleListe;
    }
}
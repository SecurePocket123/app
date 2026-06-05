package com.example.finance.data.repository;

import android.content.Context;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;
import com.example.finance.data.model.Zahlung;
import com.example.finance.data.storage.FinanceSaveData;
import com.example.finance.data.storage.FinanceStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Einfache Repository-Implementierung.
 * Mit Context werden die Daten zusätzlich in finance_data.json gespeichert.
 */
public class FinanceRepositoryImpl implements FinanceRepository {

    private FinanceStorage storage;
    private long nextPaymentId = 1;
    private long nextKategorieId = 1;

    private List<Einkommen> einkommenListe = new ArrayList<>();
    private List<Ausgaben> ausgabenListe = new ArrayList<>();
    private List<Kategorie> kategorienListe = new ArrayList<>();
    private List<Sparziel> sparzieleListe = new ArrayList<>();

    public FinanceRepositoryImpl() {
    }

    public FinanceRepositoryImpl(Context context) {
        storage = new FinanceStorage(context);
        loadFromStorage();
    }

    @Override
    public void addEinkommen(Einkommen einkommen) {
        einkommen.setId(nextPaymentId++);
        einkommenListe.add(einkommen);
        saveToStorage();
    }

    @Override
    public void addAusgaben(Ausgaben ausgaben) {
        ausgaben.setId(nextPaymentId++);
        ausgabenListe.add(ausgaben);
        saveToStorage();
    }

    @Override
    public void addKategorie(Kategorie kategorie) {
        kategorie.setId(nextKategorieId++);
        kategorienListe.add(kategorie);
        saveToStorage();
    }

    @Override
    public void addSparziel(Sparziel sparziel) {
        sparzieleListe.add(sparziel);
        saveToStorage();
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
    public List<Zahlung> getZahlungen(int monat, int jahr) {
        List<Zahlung> zahlungen = new ArrayList<>();

        for (Einkommen einkommen : getEinkommen(monat, jahr)) {
            zahlungen.add(new Zahlung(
                    einkommen.getId(),
                    einkommen.getKategorieId(),
                    getKategorieName(einkommen.getKategorieId()),
                    einkommen.getBeschreibung(),
                    einkommen.getDatum(),
                    einkommen.getBetrag(),
                    true
            ));
        }

        for (Ausgaben ausgabe : getAusgaben(monat, jahr)) {
            zahlungen.add(new Zahlung(
                    ausgabe.getId(),
                    ausgabe.getKategorieId(),
                    getKategorieName(ausgabe.getKategorieId()),
                    ausgabe.getBeschreibung(),
                    ausgabe.getDatum(),
                    ausgabe.getBetrag(),
                    false
            ));
        }

        sortiereZahlungenNachId(zahlungen);
        return zahlungen;
    }

    @Override
    public List<Zahlung> getAlleZahlungen() {
        List<Zahlung> zahlungen = new ArrayList<>();

        for (Einkommen einkommen : einkommenListe) {
            zahlungen.add(new Zahlung(
                    einkommen.getId(),
                    einkommen.getKategorieId(),
                    getKategorieName(einkommen.getKategorieId()),
                    einkommen.getBeschreibung(),
                    einkommen.getDatum(),
                    einkommen.getBetrag(),
                    true
            ));
        }

        for (Ausgaben ausgabe : ausgabenListe) {
            zahlungen.add(new Zahlung(
                    ausgabe.getId(),
                    ausgabe.getKategorieId(),
                    getKategorieName(ausgabe.getKategorieId()),
                    ausgabe.getBeschreibung(),
                    ausgabe.getDatum(),
                    ausgabe.getBetrag(),
                    false
            ));
        }

        sortiereZahlungenNachId(zahlungen);
        return zahlungen;
    }

    @Override
    public List<Kategorie> getKategorien() {
        return kategorienListe;
    }

    @Override
    public List<Sparziel> getSparziele() {
        return sparzieleListe;
    }

    private void loadFromStorage() {
        if (storage == null) {
            return;
        }

        FinanceSaveData data = storage.load();
        nextPaymentId = data.getNextPaymentId();
        nextKategorieId = data.getNextKategorieId();
        einkommenListe = data.getEinkommenListe();
        ausgabenListe = data.getAusgabenListe();
        kategorienListe = data.getKategorienListe();
        sparzieleListe = data.getSparzieleListe();
    }

    private void saveToStorage() {
        if (storage == null) {
            return;
        }

        FinanceSaveData data = new FinanceSaveData();
        data.setNextPaymentId(nextPaymentId);
        data.setNextKategorieId(nextKategorieId);
        data.setEinkommenListe(einkommenListe);
        data.setAusgabenListe(ausgabenListe);
        data.setKategorienListe(kategorienListe);
        data.setSparzieleListe(sparzieleListe);
        storage.save(data);
    }

    private void sortiereZahlungenNachId(List<Zahlung> zahlungen) {
        Collections.sort(zahlungen, new Comparator<Zahlung>() {
            @Override
            public int compare(Zahlung zahlung1, Zahlung zahlung2) {
                return Long.compare(zahlung2.getId(), zahlung1.getId());
            }
        });
    }

    private String getKategorieName(long kategorieId) {
        for (Kategorie kategorie : kategorienListe) {
            if (kategorie.getId() == kategorieId) {
                return kategorie.getName();
            }
        }
        return "Kategorie " + kategorieId;
    }
}

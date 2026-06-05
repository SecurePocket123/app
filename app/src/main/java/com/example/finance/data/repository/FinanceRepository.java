package com.example.finance.data.repository;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;

import java.util.List;

/**
 * Schnittstelle für den Datenzugriff.
 * UI und UseCases sollen nur dieses Interface kennen.
 */
public interface FinanceRepository {

    void addEinkommen(Einkommen einkommen);

    void addAusgaben(Ausgaben ausgaben);

    void addKategorie(Kategorie kategorie);

    void addSparziel(Sparziel sparziel);

    List<Einkommen> getEinkommen(int monat, int jahr);

    List<Ausgaben> getAusgaben(int monat, int jahr);

    List<Kategorie> getKategorien();

    List<Sparziel> getSparziele();
}
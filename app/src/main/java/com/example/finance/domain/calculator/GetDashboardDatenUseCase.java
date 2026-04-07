package com.example.finance.domain.calculator;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.repository.FinanceRepository;

import java.util.List;

/**
 * Berechnet die Werte für das Dashboard.
 */
public class GetDashboardDatenUseCase {

    private FinanceRepository repository;

    public GetDashboardDatenUseCase(FinanceRepository repository) {
        this.repository = repository;
    }

    public DashboardDaten ausfuehren(int monat, int jahr) {

        List<Einkommen> einkommenListe = repository.getEinkommen(monat, jahr);
        List<Ausgaben> ausgabenListe = repository.getAusgaben(monat, jahr);

        double gesamteinnahmen = 0;
        double gesamtausgaben = 0;

        for (Einkommen einkommen : einkommenListe) {
            gesamteinnahmen += einkommen.getBetrag();
        }

        for (Ausgaben ausgabe : ausgabenListe) {
            gesamtausgaben += ausgabe.getBetrag();
        }

        double kontostand = gesamteinnahmen - gesamtausgaben;
        int anzahlSparziele = repository.getSparziele().size();

        return new DashboardDaten(kontostand, gesamteinnahmen, gesamtausgaben, anzahlSparziele);
    }
}
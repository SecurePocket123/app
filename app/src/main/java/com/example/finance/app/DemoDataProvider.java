package com.example.finance.app;

import com.example.finance.data.model.LaufenderVertrag;
import com.example.finance.data.model.Sparziel;
import com.example.finance.data.repository.FinanceRepository;
import com.example.finance.domain.calculator.AddAusgabenUseCase;
import com.example.finance.domain.calculator.AddEinkommenUseCase;
import com.example.finance.domain.calculator.AddKategorieUseCase;
import com.example.finance.domain.calculator.AddSparzielUseCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kleine Demo-Daten für die UI, solange es noch keine echte Datenbank gibt.
 */
public class DemoDataProvider {

    private static boolean initialized = false;

    public static void seed(FinanceRepository repository) {
        if (initialized) {
            return;
        }

        LocalDate heute = LocalDate.now();

        AddKategorieUseCase addKategorie = new AddKategorieUseCase(repository);
        AddEinkommenUseCase addEinkommen = new AddEinkommenUseCase(repository);
        AddAusgabenUseCase addAusgaben = new AddAusgabenUseCase(repository);
        AddSparzielUseCase addSparziel = new AddSparzielUseCase(repository);

        addKategorie.ausfuehren("Gehalt", "#00FF00");
        addKategorie.ausfuehren("Essen", "#FF0000");
        addKategorie.ausfuehren("Freizeit", "#0000FF");
        addKategorie.ausfuehren("Tanken", "#FFFF00");

        addEinkommen.ausfuehren(2500.0, 1, heute, "Monatsgehalt");
        addEinkommen.ausfuehren(300.0, 1, heute, "Nebenjob");
        addEinkommen.ausfuehren(150.0, 1, heute, "Flohmarkt");

        addAusgaben.ausfuehren(50.0, 2, heute, "Rewe", "Karte");
        addAusgaben.ausfuehren(30.0, 3, heute, "Kino", "Bar");
        addAusgaben.ausfuehren(80.0, 4, heute, "Tanken", "Karte");
        addAusgaben.ausfuehren(20.0, 2, heute, "Bäckerei", "Bar");

        addSparziel.ausfuehren("Neues Handy", 1000.0, heute.plusMonths(3));
        addSparziel.ausfuehren("Urlaub", 1500.0, heute.plusMonths(6));
        addSparziel.ausfuehren("Laptop", 1200.0, heute.plusMonths(4));

        if (!repository.getSparziele().isEmpty()) {
            Sparziel sparziel = repository.getSparziele().get(0);
            sparziel.setAktuellerBetrag(300.0);
        }

        initialized = true;
    }

    public static List<LaufenderVertrag> getBeispielVertraege() {
        LocalDate monatsStart = LocalDate.now().withDayOfMonth(1);
        List<LaufenderVertrag> vertraege = new ArrayList<>();

        vertraege.add(new LaufenderVertrag("Miete", "Wohnen", 650.00, 1, monatsStart.withDayOfMonth(1)));
        vertraege.add(new LaufenderVertrag("Handyvertrag", "Kommunikation", 29.99, 1, monatsStart.withDayOfMonth(5)));
        vertraege.add(new LaufenderVertrag("Netflix", "Streaming", 12.99, 1, monatsStart.withDayOfMonth(18)));
        vertraege.add(new LaufenderVertrag("Fitnessstudio", "Freizeit", 34.90, 1, monatsStart.withDayOfMonth(25)));
        vertraege.add(new LaufenderVertrag("Rundfunkbeitrag (GEZ)", "Haushalt", 55.08, 3, monatsStart.plusMonths(1).withDayOfMonth(15)));
        vertraege.add(new LaufenderVertrag("Versicherung", "Absicherung", 114.00, 6, monatsStart.plusMonths(2).withDayOfMonth(10)));

        return vertraege;
    }
}

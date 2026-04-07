package com.example.finance;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;
import com.example.finance.data.repository.FinanceRepository;
import com.example.finance.data.repository.FinanceRepositoryImpl;

import com.example.finance.domain.calculator.AddKategorieUseCase;
import com.example.finance.domain.calculator.AddEinkommenUseCase;
import com.example.finance.domain.calculator.AddAusgabenUseCase;
import com.example.finance.domain.calculator.AddSparzielUseCase;
import com.example.finance.domain.calculator.GetDashboardDatenUseCase;
import com.example.finance.domain.calculator.DashboardDaten;

import java.time.LocalDate;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MEGA_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startseite);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

        // =========================
        // 👉 HIER DEIN TESTCODE
        // =========================

        FinanceRepository repository = new FinanceRepositoryImpl();

        AddKategorieUseCase addKategorie = new AddKategorieUseCase(repository);
        AddEinkommenUseCase addEinkommen = new AddEinkommenUseCase(repository);
        AddAusgabenUseCase addAusgaben = new AddAusgabenUseCase(repository);
        AddSparzielUseCase addSparziel = new AddSparzielUseCase(repository);
        GetDashboardDatenUseCase dashboard = new GetDashboardDatenUseCase(repository);

        LocalDate heute = LocalDate.now();
        LocalDate letzterMonat = heute.minusMonths(1);

        int aktuellerMonat = heute.getMonthValue();
        int aktuellesJahr = heute.getYear();

        Log.d(TAG, "======================================");
        Log.d(TAG, "MEGA TEST START");
        Log.d(TAG, "======================================");

        // TEST 1: Kategorien hinzufügen
        try {
            Log.d(TAG, "TEST 1: Kategorien hinzufügen");

            addKategorie.ausfuehren("Gehalt", "#00FF00");
            addKategorie.ausfuehren("Essen", "#FF0000");
            addKategorie.ausfuehren("Freizeit", "#0000FF");
            addKategorie.ausfuehren("Tanken", "#FFFF00");

            List<Kategorie> kategorien = repository.getKategorien();
            Log.d(TAG, "Anzahl Kategorien: " + kategorien.size());

            for (Kategorie kategorie : kategorien) {
                Log.d(TAG, "Kategorie -> Name: " + kategorie.getName() + ", Farbe: " + kategorie.getFarbe());
            }

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 1", e);
        }

        // TEST 2: Einkommen hinzufügen
        try {
            Log.d(TAG, "TEST 2: Einkommen hinzufügen");

            addEinkommen.ausfuehren(2500.0, 1, heute, "Monatsgehalt");
            addEinkommen.ausfuehren(300.0, 1, heute, "Nebenjob");
            addEinkommen.ausfuehren(150.0, 1, heute, "Flohmarkt");

            List<Einkommen> einkommenListe = repository.getEinkommen(aktuellerMonat, aktuellesJahr);
            Log.d(TAG, "Anzahl Einkommen aktueller Monat: " + einkommenListe.size());

            for (Einkommen einkommen : einkommenListe) {
                Log.d(TAG, "Einkommen -> Betrag: " + einkommen.getBetrag()
                        + ", KategorieId: " + einkommen.getKategorieId()
                        + ", Datum: " + einkommen.getDatum()
                        + ", Beschreibung: " + einkommen.getBeschreibung());
            }

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 2", e);
        }

        // TEST 3: Ausgaben hinzufügen
        try {
            Log.d(TAG, "TEST 3: Ausgaben hinzufügen");

            addAusgaben.ausfuehren(50.0, 2, heute, "Rewe", "Karte");
            addAusgaben.ausfuehren(30.0, 3, heute, "Kino", "Bar");
            addAusgaben.ausfuehren(80.0, 4, heute, "Tanken", "Karte");
            addAusgaben.ausfuehren(20.0, 2, heute, "Bäckerei", "Bar");

            List<Ausgaben> ausgabenListe = repository.getAusgaben(aktuellerMonat, aktuellesJahr);
            Log.d(TAG, "Anzahl Ausgaben aktueller Monat: " + ausgabenListe.size());

            for (Ausgaben ausgabe : ausgabenListe) {
                Log.d(TAG, "Ausgabe -> Betrag: " + ausgabe.getBetrag()
                        + ", KategorieId: " + ausgabe.getKategorieId()
                        + ", Datum: " + ausgabe.getDatum()
                        + ", Beschreibung: " + ausgabe.getBeschreibung()
                        + ", Zahlungsart: " + ausgabe.getZahlungsart());
            }

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 3", e);
        }

        // TEST 4: Sparziele hinzufügen
        try {
            Log.d(TAG, "TEST 4: Sparziele hinzufügen");

            addSparziel.ausfuehren("Neues Handy", 1000.0, heute.plusMonths(3));
            addSparziel.ausfuehren("Urlaub", 1500.0, heute.plusMonths(6));
            addSparziel.ausfuehren("Laptop", 1200.0, heute.plusMonths(4));

            List<Sparziel> sparziele = repository.getSparziele();
            Log.d(TAG, "Anzahl Sparziele: " + sparziele.size());

            for (Sparziel sparziel : sparziele) {
                Log.d(TAG, "Sparziel -> Name: " + sparziel.getName()
                        + ", Zielbetrag: " + sparziel.getZielbetrag()
                        + ", Aktueller Betrag: " + sparziel.getAktuellerBetrag()
                        + ", Zieldatum: " + sparziel.getZieldatum()
                        + ", Fortschritt: " + sparziel.getFortschrittInProzent());
            }

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 4", e);
        }

        // TEST 5: Dashboard aktueller Monat
        try {
            Log.d(TAG, "TEST 5: Dashboard aktueller Monat");

            DashboardDaten daten = dashboard.ausfuehren(aktuellerMonat, aktuellesJahr);

            Log.d(TAG, "Dashboard Kontostand: " + daten.getKontostand());
            Log.d(TAG, "Dashboard Einnahmen: " + daten.getGesamteinnahmen());
            Log.d(TAG, "Dashboard Ausgaben: " + daten.getGesamtausgaben());
            Log.d(TAG, "Dashboard Sparziele: " + daten.getAnzahlSparziele());

            Log.d(TAG, "ERWARTET -> Einnahmen: 2950.0");
            Log.d(TAG, "ERWARTET -> Ausgaben: 180.0");
            Log.d(TAG, "ERWARTET -> Kontostand: 2770.0");

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 5", e);
        }

        // TEST 6: Monatsfilter
        try {
            Log.d(TAG, "TEST 6: Monatsfilter");

            addEinkommen.ausfuehren(999.0, 1, letzterMonat, "Letzter Monat Einkommen");
            addAusgaben.ausfuehren(111.0, 2, letzterMonat, "Letzter Monat Ausgabe", "Bar");

            DashboardDaten datenAktuell = dashboard.ausfuehren(aktuellerMonat, aktuellesJahr);
            DashboardDaten datenLetzterMonat = dashboard.ausfuehren(
                    letzterMonat.getMonthValue(),
                    letzterMonat.getYear()
            );

            Log.d(TAG, "Aktueller Monat Einnahmen: " + datenAktuell.getGesamteinnahmen());
            Log.d(TAG, "Aktueller Monat Ausgaben: " + datenAktuell.getGesamtausgaben());
            Log.d(TAG, "Letzter Monat Einnahmen: " + datenLetzterMonat.getGesamteinnahmen());
            Log.d(TAG, "Letzter Monat Ausgaben: " + datenLetzterMonat.getGesamtausgaben());

            Log.d(TAG, "ERWARTET aktueller Monat -> Einnahmen: 2950.0");
            Log.d(TAG, "ERWARTET aktueller Monat -> Ausgaben: 180.0");
            Log.d(TAG, "ERWARTET letzter Monat -> Einnahmen: 999.0");
            Log.d(TAG, "ERWARTET letzter Monat -> Ausgaben: 111.0");

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 6", e);
        }

        // TEST 7: Ungültiges Einkommen
        try {
            Log.d(TAG, "TEST 7: Fehlerfall Einkommen");
            addEinkommen.ausfuehren(-10.0, 1, heute, "Ungültig");
            Log.e(TAG, "FEHLER: Negatives Einkommen wurde akzeptiert");
        } catch (Exception e) {
            Log.d(TAG, "OK: Negatives Einkommen abgefangen -> " + e.getMessage());
        }

        // TEST 8: Ungültige Ausgabe
        try {
            Log.d(TAG, "TEST 8: Fehlerfall Ausgaben");
            addAusgaben.ausfuehren(-20.0, 2, heute, "Ungültig", "Bar");
            Log.e(TAG, "FEHLER: Negative Ausgabe wurde akzeptiert");
        } catch (Exception e) {
            Log.d(TAG, "OK: Negative Ausgabe abgefangen -> " + e.getMessage());
        }

        // TEST 9: Leere Kategorie
        try {
            Log.d(TAG, "TEST 9: Fehlerfall Kategorie");
            addKategorie.ausfuehren("", "#123456");
            Log.e(TAG, "FEHLER: Leere Kategorie wurde akzeptiert");
        } catch (Exception e) {
            Log.d(TAG, "OK: Leere Kategorie abgefangen -> " + e.getMessage());
        }

        // TEST 10: Fehlerhafte Sparziele
        try {
            Log.d(TAG, "TEST 10A: Leerer Sparzielname");
            addSparziel.ausfuehren("", 500.0, heute.plusMonths(1));
            Log.e(TAG, "FEHLER: Leeres Sparziel wurde akzeptiert");
        } catch (Exception e) {
            Log.d(TAG, "OK: Leeres Sparziel abgefangen -> " + e.getMessage());
        }

        try {
            Log.d(TAG, "TEST 10B: Negativer Zielbetrag");
            addSparziel.ausfuehren("Fehltest", -500.0, heute.plusMonths(1));
            Log.e(TAG, "FEHLER: Negativer Zielbetrag wurde akzeptiert");
        } catch (Exception e) {
            Log.d(TAG, "OK: Negativer Zielbetrag abgefangen -> " + e.getMessage());
        }

        // TEST 11: Gesamtstände im Repository
        try {
            Log.d(TAG, "TEST 11: Gesamtprüfung Repository");

            Log.d(TAG, "Kategorien gesamt: " + repository.getKategorien().size());
            Log.d(TAG, "Sparziele gesamt: " + repository.getSparziele().size());
            Log.d(TAG, "Einkommen aktueller Monat: " + repository.getEinkommen(aktuellerMonat, aktuellesJahr).size());
            Log.d(TAG, "Ausgaben aktueller Monat: " + repository.getAusgaben(aktuellerMonat, aktuellesJahr).size());

        } catch (Exception e) {
            Log.e(TAG, "FEHLER in TEST 11", e);
        }

        Log.d(TAG, "======================================");
        Log.d(TAG, "MEGA TEST ENDE");
        Log.d(TAG, "======================================");
    }
}
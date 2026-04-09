package com.example.finance.domain.calculator;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.repository.FinanceRepository;

import java.time.LocalDate;

/**
 * Fügt eine Ausgabe hinzu und prüft die Eingaben.
 */
public class AddAusgabenUseCase {

    private FinanceRepository repository;

    public AddAusgabenUseCase(FinanceRepository repository) {
        this.repository = repository;
    }

    public void ausfuehren(double betrag, long kategorieId, LocalDate datum,
                           String beschreibung, String zahlungsart) {

        if (betrag <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein.");
        }

        if (datum == null) {
            throw new IllegalArgumentException("Datum darf nicht leer sein.");
        }

        Ausgaben ausgaben = new Ausgaben(betrag, kategorieId, datum, beschreibung, zahlungsart);
        repository.addAusgaben(ausgaben);
    }
}
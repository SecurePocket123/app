package com.example.finance.domain.calculator;

import java.time.LocalDate;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.repository.FinanceRepository;


/**
 * Fügt ein Einkommen hinzu und prüft die Eingaben.
 */
public class AddEinkommenUseCase {

    private FinanceRepository repository;

    public AddEinkommenUseCase(FinanceRepository repository) {
        this.repository = repository;
    }

    public void ausfuehren(double betrag, long kategorieId, LocalDate datum, String beschreibung) {

        if (betrag <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein.");
        }

        if (datum == null) {
            throw new IllegalArgumentException("Datum darf nicht leer sein.");
        }

        Einkommen einkommen = new Einkommen(betrag, kategorieId, datum, beschreibung);
        repository.addEinkommen(einkommen);
    }
}
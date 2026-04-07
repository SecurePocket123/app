package com.example.finance.domain.calculator;

import com.example.finance.data.model.Sparziel;
import com.example.finance.data.repository.FinanceRepository;

import java.time.LocalDate;

/**
 * Fügt ein Sparziel hinzu.
 */
public class AddSparzielUseCase {

    private FinanceRepository repository;

    public AddSparzielUseCase(FinanceRepository repository) {
        this.repository = repository;
    }

    public void ausfuehren(String name, double zielbetrag, LocalDate zieldatum) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name des Sparziels darf nicht leer sein.");
        }

        if (zielbetrag <= 0) {
            throw new IllegalArgumentException("Zielbetrag muss größer als 0 sein.");
        }

        if (zieldatum == null) {
            throw new IllegalArgumentException("Zieldatum darf nicht leer sein.");
        }

        Sparziel sparziel = new Sparziel(name, zielbetrag, zieldatum);
        repository.addSparziel(sparziel);
    }
}
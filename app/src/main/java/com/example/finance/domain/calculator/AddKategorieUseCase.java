package com.example.finance.domain.calculator;

import com.example.finance.data.model.Kategorie;
import com.example.finance.data.repository.FinanceRepository;

/**
 * Fügt eine Kategorie hinzu.
 */
public class AddKategorieUseCase {

    private FinanceRepository repository;

    public AddKategorieUseCase(FinanceRepository repository) {
        this.repository = repository;
    }

    public void ausfuehren(String name, String farbe) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategoriename darf nicht leer sein.");
        }

        if (farbe == null || farbe.trim().isEmpty()) {
            throw new IllegalArgumentException("Farbe darf nicht leer sein.");
        }

        Kategorie kategorie = new Kategorie(name, farbe);
        repository.addKategorie(kategorie);
    }
}
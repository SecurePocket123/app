package com.example.finance.app;

import com.example.finance.data.repository.FinanceRepository;
import com.example.finance.data.repository.FinanceRepositoryImpl;

/**
 * Einfache zentrale Stelle für gemeinsame App-Abhängigkeiten.
 */
public class AppContainer {

    private static final FinanceRepository repository = new FinanceRepositoryImpl();

    public static FinanceRepository getRepository() {
        return repository;
    }
}

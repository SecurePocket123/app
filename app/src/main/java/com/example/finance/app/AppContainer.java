package com.example.finance.app;

import android.content.Context;

import com.example.finance.data.repository.FinanceRepository;
import com.example.finance.data.repository.FinanceRepositoryImpl;

/**
 * Einfache zentrale Stelle für gemeinsame App-Abhängigkeiten.
 */
public class AppContainer {

    private static FinanceRepository repository;

    public static void init(Context context) {
        if (repository == null) {
            repository = new FinanceRepositoryImpl(context);
        }
    }

    public static FinanceRepository getRepository() {
        if (repository == null) {
            repository = new FinanceRepositoryImpl();
        }
        return repository;
    }
}

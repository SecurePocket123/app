package com.example.finance.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.finance.app.AppContainer;
import com.example.finance.domain.calculator.DashboardDaten;
import com.example.finance.domain.calculator.GetDashboardDatenUseCase;

import java.time.LocalDate;

public class UebersichtViewModel extends ViewModel {

    private final GetDashboardDatenUseCase dashboardUseCase =
            new GetDashboardDatenUseCase(AppContainer.getRepository());

    public DashboardDaten getDashboardDaten() {
        LocalDate heute = LocalDate.now();
        return dashboardUseCase.ausfuehren(heute.getMonthValue(), heute.getYear());
    }
}

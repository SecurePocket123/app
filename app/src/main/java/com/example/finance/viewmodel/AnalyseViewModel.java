package com.example.finance.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.finance.app.DemoDataProvider;
import com.example.finance.data.model.LaufenderVertrag;
import com.example.finance.domain.calculator.GetLaufendeVertraegeUseCase;
import com.example.finance.domain.calculator.VertragsAnalyseDaten;

import java.time.LocalDate;
import java.util.List;

public class AnalyseViewModel extends ViewModel {

    private final GetLaufendeVertraegeUseCase useCase = new GetLaufendeVertraegeUseCase();

    public VertragsAnalyseDaten getVertragsAnalyseDaten() {
        LocalDate heute = LocalDate.now();
        List<LaufenderVertrag> vertraege = DemoDataProvider.getBeispielVertraege();
        return useCase.ausfuehren(vertraege, heute.getMonthValue(), heute.getYear());
    }
}

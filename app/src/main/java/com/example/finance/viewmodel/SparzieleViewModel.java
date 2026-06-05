package com.example.finance.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.finance.app.AppContainer;
import com.example.finance.data.model.Sparziel;

import java.util.List;

public class SparzieleViewModel extends ViewModel {

    public Sparziel getAktuellesSparziel() {
        List<Sparziel> sparziele = AppContainer.getRepository().getSparziele();
        if (sparziele.isEmpty()) {
            return null;
        }
        return sparziele.get(0);
    }
}

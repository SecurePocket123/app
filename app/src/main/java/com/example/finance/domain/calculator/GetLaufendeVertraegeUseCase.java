package com.example.finance.domain.calculator;

import com.example.finance.data.model.LaufenderVertrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Filtert, sortiert und berechnet laufende Verträge für die Analyse-Seite.
 */
public class GetLaufendeVertraegeUseCase {

    public VertragsAnalyseDaten ausfuehren(List<LaufenderVertrag> alleVertraege, int monat, int jahr) {
        List<LaufenderVertrag> aktuelleVertraege = new ArrayList<>();
        List<LaufenderVertrag> andereVertraege = new ArrayList<>();

        if (alleVertraege == null) {
            alleVertraege = new ArrayList<>();
        }

        for (LaufenderVertrag vertrag : alleVertraege) {
            if (vertrag.istFaelligInMonat(monat, jahr)) {
                aktuelleVertraege.add(vertrag);
            } else {
                andereVertraege.add(vertrag);
            }
        }

        sortiereNachNaechsterAbbuchung(aktuelleVertraege);
        sortiereNachNaechsterAbbuchung(andereVertraege);

        double aktuellerGesamtbetrag = berechneGesamtbetrag(aktuelleVertraege);
        double sonstigerMonatsdurchschnitt = berechneMonatlichenDurchschnitt(andereVertraege);

        return new VertragsAnalyseDaten(
                aktuelleVertraege,
                andereVertraege,
                aktuellerGesamtbetrag,
                sonstigerMonatsdurchschnitt
        );
    }

    private void sortiereNachNaechsterAbbuchung(List<LaufenderVertrag> vertraege) {
        Collections.sort(vertraege, new Comparator<LaufenderVertrag>() {
            @Override
            public int compare(LaufenderVertrag vertrag1, LaufenderVertrag vertrag2) {
                int datumVergleich = vertrag1.getNaechsteAbbuchung().compareTo(vertrag2.getNaechsteAbbuchung());

                if (datumVergleich != 0) {
                    return datumVergleich;
                }

                return vertrag1.getName().compareToIgnoreCase(vertrag2.getName());
            }
        });
    }

    private double berechneGesamtbetrag(List<LaufenderVertrag> vertraege) {
        double summe = 0;

        for (LaufenderVertrag vertrag : vertraege) {
            summe += vertrag.getBetrag();
        }

        return summe;
    }

    private double berechneMonatlichenDurchschnitt(List<LaufenderVertrag> vertraege) {
        double summe = 0;

        for (LaufenderVertrag vertrag : vertraege) {
            summe += vertrag.getMonatlicherDurchschnitt();
        }

        return summe;
    }
}
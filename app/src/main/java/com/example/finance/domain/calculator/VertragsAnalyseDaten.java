package com.example.finance.domain.calculator;

import com.example.finance.data.model.LaufenderVertrag;

import java.util.List;

/**
 * Ergebnisobjekt für die Analyse-Seite.
 * Enthält bereits getrennte und berechnete Vertragsdaten für die UI.
 */
public class VertragsAnalyseDaten {

    private List<LaufenderVertrag> aktuelleVertraege;
    private List<LaufenderVertrag> andereVertraege;
    private double aktuellerGesamtbetrag;
    private double sonstigerMonatsdurchschnitt;

    public VertragsAnalyseDaten(List<LaufenderVertrag> aktuelleVertraege,
                                List<LaufenderVertrag> andereVertraege,
                                double aktuellerGesamtbetrag,
                                double sonstigerMonatsdurchschnitt) {
        this.aktuelleVertraege = aktuelleVertraege;
        this.andereVertraege = andereVertraege;
        this.aktuellerGesamtbetrag = aktuellerGesamtbetrag;
        this.sonstigerMonatsdurchschnitt = sonstigerMonatsdurchschnitt;
    }

    public List<LaufenderVertrag> getAktuelleVertraege() {
        return aktuelleVertraege;
    }

    public List<LaufenderVertrag> getAndereVertraege() {
        return andereVertraege;
    }

    public double getAktuellerGesamtbetrag() {
        return aktuellerGesamtbetrag;
    }

    public double getSonstigerMonatsdurchschnitt() {
        return sonstigerMonatsdurchschnitt;
    }
}
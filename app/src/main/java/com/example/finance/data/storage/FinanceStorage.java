package com.example.finance.data.storage;

import android.content.Context;

import com.example.finance.data.model.Ausgaben;
import com.example.finance.data.model.Einkommen;
import com.example.finance.data.model.Kategorie;
import com.example.finance.data.model.Sparziel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceStorage {

    private static final String FILE_NAME = "finance_data.json";

    private Context context;

    public FinanceStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    public FinanceSaveData load() {
        FinanceSaveData data = new FinanceSaveData();

        try {
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder jsonText = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }

            reader.close();
            JSONObject root = new JSONObject(jsonText.toString());

            data.setNextPaymentId(root.optLong("nextPaymentId", 1));
            data.setNextKategorieId(root.optLong("nextKategorieId", 1));
            data.setKategorienListe(readKategorien(root.optJSONArray("kategorien")));
            data.setEinkommenListe(readEinkommen(root.optJSONArray("einkommen")));
            data.setAusgabenListe(readAusgaben(root.optJSONArray("ausgaben")));
            data.setSparzieleListe(readSparziele(root.optJSONArray("sparziele")));
        } catch (Exception ignored) {
            // Wenn die Datei noch nicht existiert oder beschädigt ist, startet die App leer.
        }

        return data;
    }

    public void save(FinanceSaveData data) {
        try {
            JSONObject root = new JSONObject();
            root.put("nextPaymentId", data.getNextPaymentId());
            root.put("nextKategorieId", data.getNextKategorieId());
            root.put("kategorien", writeKategorien(data.getKategorienListe()));
            root.put("einkommen", writeEinkommen(data.getEinkommenListe()));
            root.put("ausgaben", writeAusgaben(data.getAusgabenListe()));
            root.put("sparziele", writeSparziele(data.getSparzieleListe()));

            FileOutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(root.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception ignored) {
            // Speichern soll die UI nicht crashen lassen.
        }
    }

    private List<Kategorie> readKategorien(JSONArray array) throws Exception {
        List<Kategorie> kategorien = new ArrayList<>();
        if (array == null) return kategorien;

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Kategorie kategorie = new Kategorie(object.optString("name"), object.optString("farbe"));
            kategorie.setId(object.optLong("id"));
            kategorien.add(kategorie);
        }

        return kategorien;
    }

    private List<Einkommen> readEinkommen(JSONArray array) throws Exception {
        List<Einkommen> einkommenListe = new ArrayList<>();
        if (array == null) return einkommenListe;

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Einkommen einkommen = new Einkommen(
                    object.optDouble("betrag"),
                    object.optLong("kategorieId"),
                    LocalDate.parse(object.optString("datum")),
                    object.optString("beschreibung")
            );
            einkommen.setId(object.optLong("id"));
            einkommenListe.add(einkommen);
        }

        return einkommenListe;
    }

    private List<Ausgaben> readAusgaben(JSONArray array) throws Exception {
        List<Ausgaben> ausgabenListe = new ArrayList<>();
        if (array == null) return ausgabenListe;

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Ausgaben ausgabe = new Ausgaben(
                    object.optDouble("betrag"),
                    object.optLong("kategorieId"),
                    LocalDate.parse(object.optString("datum")),
                    object.optString("beschreibung"),
                    object.optString("zahlungsart")
            );
            ausgabe.setId(object.optLong("id"));
            ausgabenListe.add(ausgabe);
        }

        return ausgabenListe;
    }

    private List<Sparziel> readSparziele(JSONArray array) throws Exception {
        List<Sparziel> sparziele = new ArrayList<>();
        if (array == null) return sparziele;

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Sparziel sparziel = new Sparziel(
                    object.optString("name"),
                    object.optDouble("zielbetrag"),
                    LocalDate.parse(object.optString("zieldatum"))
            );
            sparziel.setAktuellerBetrag(object.optDouble("aktuellerBetrag"));
            sparziele.add(sparziel);
        }

        return sparziele;
    }

    private JSONArray writeKategorien(List<Kategorie> kategorien) throws Exception {
        JSONArray array = new JSONArray();
        for (Kategorie kategorie : kategorien) {
            JSONObject object = new JSONObject();
            object.put("id", kategorie.getId());
            object.put("name", kategorie.getName());
            object.put("farbe", kategorie.getFarbe());
            array.put(object);
        }
        return array;
    }

    private JSONArray writeEinkommen(List<Einkommen> einkommenListe) throws Exception {
        JSONArray array = new JSONArray();
        for (Einkommen einkommen : einkommenListe) {
            JSONObject object = new JSONObject();
            object.put("id", einkommen.getId());
            object.put("betrag", einkommen.getBetrag());
            object.put("kategorieId", einkommen.getKategorieId());
            object.put("datum", einkommen.getDatum().toString());
            object.put("beschreibung", einkommen.getBeschreibung());
            array.put(object);
        }
        return array;
    }

    private JSONArray writeAusgaben(List<Ausgaben> ausgabenListe) throws Exception {
        JSONArray array = new JSONArray();
        for (Ausgaben ausgabe : ausgabenListe) {
            JSONObject object = new JSONObject();
            object.put("id", ausgabe.getId());
            object.put("betrag", ausgabe.getBetrag());
            object.put("kategorieId", ausgabe.getKategorieId());
            object.put("datum", ausgabe.getDatum().toString());
            object.put("beschreibung", ausgabe.getBeschreibung());
            object.put("zahlungsart", ausgabe.getZahlungsart());
            array.put(object);
        }
        return array;
    }

    private JSONArray writeSparziele(List<Sparziel> sparziele) throws Exception {
        JSONArray array = new JSONArray();
        for (Sparziel sparziel : sparziele) {
            JSONObject object = new JSONObject();
            object.put("id", sparziel.getId());
            object.put("name", sparziel.getName());
            object.put("zielbetrag", sparziel.getZielbetrag());
            object.put("aktuellerBetrag", sparziel.getAktuellerBetrag());
            object.put("zieldatum", sparziel.getZieldatum().toString());
            array.put(object);
        }
        return array;
    }
}

# Testing

## 1. In Android Studio testen

1. Projekt in Android Studio öffnen.
2. Warten, bis Gradle Sync fertig ist.
3. Oben als Run Configuration `app` auswählen.
4. Emulator oder echtes Android-Gerät starten.
5. Auf **Run** klicken.

## 2. Was nach dem Start sichtbar sein sollte

Die Demo-Daten werden beim App-Start in `MainActivity` über `DemoDataProvider.seed(AppContainer.getRepository())` geladen.

Erwartete Demo-Werte:

- Einnahmen: `2950,00 €`
- Ausgaben: `180,00 €`
- Kontostand: `2770,00 €`
- Erstes Sparziel: `Neues Handy` mit Zielbetrag `1000,00 €` und aktuellem Betrag `300,00 €`

## 3. Terminal-Checks lokal ausführen

Im Projektordner:

```bash
./gradlew clean build
```

Nur Java-Kompilierung der Debug-App:

```bash
./gradlew :app:compileDebugJavaWithJavac
```

Unit Tests:

```bash
./gradlew test
```

Instrumented Tests mit Emulator/Gerät:

```bash
./gradlew connectedAndroidTest
```

## 4. Wenn Gradle nicht startet

Falls `Permission denied` kommt:

```bash
chmod +x ./gradlew
./gradlew build
```

Falls der Gradle-Download blockiert wird, liegt es meistens am Netzwerk/Proxy. Dann Android Studio öffnen oder ein anderes Netzwerk verwenden, damit diese Datei heruntergeladen werden kann:

```text
https://services.gradle.org/distributions/gradle-9.1.0-bin.zip
```

## 5. Wo die neue Verknüpfung liegt

- Gemeinsames Repository: `app/src/main/java/com/example/finance/app/AppContainer.java`
- Demo-Daten: `app/src/main/java/com/example/finance/app/DemoDataProvider.java`
- App-Start/Seed: `app/src/main/java/com/example/finance/MainActivity.java`
- ViewModels: `app/src/main/java/com/example/finance/viewmodel/`

## 6. Lokale Speicherung

Die App speichert Daten jetzt automatisch intern in dieser Datei:

```text
finance_data.json
```

Die Datei liegt im privaten App-Speicher. Android-Nutzer sehen sie normalerweise nicht direkt im Dateimanager.

Verhalten:

- Beim App-Start wird `finance_data.json` geladen.
- Nach neuen Einnahmen, Ausgaben, Kategorien oder Sparzielen wird automatisch gespeichert.
- Demo-Daten werden nur eingefügt, wenn noch keine gespeicherten Daten vorhanden sind.

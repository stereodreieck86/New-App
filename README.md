# Creator Desk

**Creator Desk** ist eine moderne, lokal laufende Android-App für Content Creator, mit der sich Video-Ideen sammeln, wirkungsvolle Video-Einstiege (Hooks) schreiben, Beiträge im Kalender planen und der eigene Produktionsfortschritt übersichtlich verfolgen lassen – vollständig offline und ohne Cloud-Zwang.

![Platform](https://img.shields.io/badge/Plattform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Sprache-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Storage](https://img.shields.io/badge/Speicherung-Room%20%26%20DataStore-E04832?style=flat-square)
![MinSDK](https://img.shields.io/badge/minSdk-24%20(Android%207.0)-198263?style=flat-square)

---

## Inhaltsverzeichnis

- [Funktionsumfang](#funktionsumfang)
- [Unterstützte Plattformen & Workflow](#unterstützte-plattformen--workflow)
- [Architektur & Tech-Stack](#architektur--tech-stack)
- [Projektstruktur](#projektstruktur)
- [Tests & Qualitätssicherung](#tests--qualitätssicherung)
- [Voraussetzungen & Build](#voraussetzungen--build)
- [Datenschutz & lokale Datenspeicherung](#datenschutz--lokale-datenspeicherung)
- [Mögliche Erweiterungen](#mögliche-erweiterungen)

---

## Funktionsumfang

### 1. Dashboard & Fortschrittsverfolgung
- **Wochenziel-Fortschrittsring**: Verfolge auf einen Blick, wie viele Beiträge du in der aktuellen Woche bereits veröffentlicht hast (zeitzonengenau über ein dediziertes Veröffentlichungsdatum). Das Wochenziel lässt sich flexibel zwischen **1 und 14 Beiträgen pro Woche** einstellen.
- **KPI-Übersicht (Bento-Grid)**: Direkte Kennzahlen zu *Ideen gesamt*, *Hooks bereit*, *Geplant* und *Veröffentlicht* – jede Karte dient gleichzeitig als Schnellzugriff.
- **Produktions-Pipeline**: Eine proportionale Fortschrittsleiste und interaktive Status-Kacheln zeigen die Verteilung über alle 5 Produktionsphasen hinweg. Ein Tipp auf eine Phase filtert direkt die zugehörigen Ideen.
- **Anstehende Veröffentlichungen & Entwürfe**: Zeigt chronologisch die nächsten geplanten Termine sowie aktuelle Ideen mit 1-Tap-Statuswechsel (*Nächster Schritt*).

### 2. Content-Ideen, Editor & Live-Suche
- **Ideenverwaltung**: Erstelle, bearbeite, hefte an (*Pin*) und lösche Video- und Beitrags-Ideen inklusive Titel, Plattform, Hook, Status, Veröffentlichungstermin, frei wählbarer Uhrzeit (Schnellauswahl + freie Eingabe) und ausführlichen Skript-/CTA-Notizen.
- **Rotationssicherer Editor**: Ungespeicherte Eingaben im Modal-Editor werden im `ViewModel` gehalten und bleiben auch bei Bildschirmrotationen oder Split-Screen-Änderungen erhalten.
- **Echtzeit-Suche & kombinierbare Filter**: Durchsuche sekundenschnell Titel, Hooks, Notizen, Plattformen und Statusbezeichnungen und kombiniere sie mit Plattform- sowie Status-Filtern.
- **Schneller Fortschritt**: Mit einem Klick auf *Nächster Schritt* wandert ein Beitrag direkt in die nächste Produktionsstufe.

### 3. Video-Einstiege (Hook-Studio)
- **Bewährte Hook-Formeln**: Vorgefertigte deutsche Hook-Vorlagen (*Das übersehene Detail*, *Musterbrecher*, *Kompakte Liste*, *Vorher / Nachher*, *Direktes Problem*) liefern sofortige Starthilfe für die entscheidenden ersten Sekunden.
- **Sprechzeit- & Wortzähler**: Während des Schreibens berechnet die App live die Wortanzahl und die geschätzte Sprechdauer (z. B. *ca. 3 Sek. · 8 Wörter*), um prägnante Einstiege für die 3-Sekunden-Regel zu optimieren.
- **Hook-Stile & Zwischenablage**: Ordne Hooks nach Stil (*Neugier*, *Kontrovers*, *Story*, *Liste*, *Problem*, *Frei*) und kopiere fertige Einstiege mit einem Tipp in die Zwischenablage.

### 4. Content-Kalender
- **Deutscher Monatskalender (Mo–So)**: Übersichtliche Monatsansicht mit farbcodierten Plattform-Punkten an jedem geplanten Tag, barrierefreien Touch-Targets (mind. 48 dp) sowie Schnellnavigation (*Vorheriger Monat*, *Nächster Monat*, *Heute*).
- **Tagesansicht & Direktplanung**: Tippe auf ein Datum, um alle dort geplanten Beiträge zu sehen oder direkt einen neuen Beitrag für genau diesen Tag anzulegen.
- **Ungeplante Ideen schnell terminieren**: Noch nicht eingeplante Entwürfe lassen sich mit einem einzigen Tipp direkt auf das aktuell ausgewählte Datum legen.
- **Responsive Zweispaltigkeit**: Auf Tablets, Foldables und breiten Bildschirmen erscheinen Monatskalender und Tagesplan nebeneinander – inklusive direkter Tagesplanung.

### 5. Dark Mode & Modernes Design
- **Umschaltbarer Darstellungsmodus**: Wechsle direkt in der oberen Leiste zwischen **System**, **Dunkel** (Obsidian-Studio) und **Hell** (Warmes Editorial-Canvas). Die gespeicherte Auswahl wird vor dem ersten Frame geladen, sodass beim App-Start kein Theme-Flackern entsteht.
- **Edge-to-Edge & Adaptive UI**: Nutzt eine untere `NavigationBar` auf Smartphones und wechselt auf größeren Bildschirmen automatisch auf eine seitliche `NavigationRail`.
- **Individuelle Variable-Font-Typografie**: Kombiniert *Space Grotesk* (Überschriften & Kennzahlen) mit *Plus Jakarta Sans* (Fließtext & Hooks) inklusive expliziter Gewichtsvariationen (`Normal`, `Medium`, `SemiBold`, `Bold`).

---

## Unterstützte Plattformen & Workflow

### Plattformen
| Plattform | Kurzform | Einsatzbereich |
| :--- | :---: | :--- |
| **YouTube** | `YT` | Langformat-Videos & Tutorials |
| **TikTok** | `TT` | Kurzvideos & Trends |
| **Instagram** | `IG` | Reels, Karussells & Feed-Beiträge |
| **Shorts / Reels** | `Short` | Plattformübergreifende Vertical-Videos |
| **LinkedIn** | `IN` | Fachbeiträge & Video-Posts |
| **Podcast** | `Pod` | Episoden-Ideen & Intros |

### Produktions-Pipeline (5 Phasen)
```text
Idee ──▶ Hook & Skript ──▶ Produktion ──▶ Geplant ──▶ Veröffentlicht
```

---

## Architektur & Tech-Stack

Creator Desk folgt dem **MVVM-Architekturmuster** (Model–View–ViewModel) mit reaktivem Datenfluss:

- **Sprache**: Kotlin
- **UI-Framework**: Jetpack Compose & Material Design 3
- **State-Management**: `ViewModel`, `StateFlow` und `collectAsStateWithLifecycle()`
- **Lokale Datenbank**: AndroidX Room (`creator_desk.db`, Schema-Version 2 mit verlustfreier `MIGRATION_1_2`) via KSP und Repository-Pattern
- **Einstellungen**: Jetpack DataStore Preferences (`ThemeMode`, `weeklyGoal`)
- **Asynchronität**: Kotlin Coroutines & Flow
- **Datum & Kalender**: API-24-sichere Datumslogik (`SimpleDate` / `YearMonthKey`) mit lokaler Zeitzonen-Unterstützung
- **Typografie**: Lokal gebündelte Schriftarten (`Space Grotesk` & `Plus Jakarta Sans`) in `res/font/` mit Variable-Font-Gewichtsachsen (ab API 26)

---

## Projektstruktur

```text
app/src/
├── main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/
│   │   ├── MainActivity.kt                  # Einstiegspunkt, Edge-to-Edge & Theme-Initialisierung
│   │   ├── data/
│   │   │   ├── ContentItemEntity.kt         # Room-Entity & Domain-Mapper
│   │   │   ├── ContentItemDao.kt            # Reaktive Flow-Abfragen & Suspend-Operationen
│   │   │   ├── CreatorDeskDatabase.kt       # Room-Datenbank (v2) inkl. MIGRATION_1_2
│   │   │   ├── ContentRepository.kt         # Datenzugriffsschicht inkl. Zeitstempel-Erhalt
│   │   │   └── ThemePreferencesRepository.kt# DataStore für Dark Mode & Wochenziel
│   │   ├── model/
│   │   │   ├── ContentModels.kt             # Plattformen, Status, Hook-Stile, Vorlagen & Modelle
│   │   │   └── DateUtils.kt                 # API-24-sichere Datums- & Kalenderformatierung
│   │   ├── viewmodel/
│   │   │   └── CreatorDeskViewModel.kt      # UI-State, Filter, Kennzahlen & Editor-Entwurf
│   │   └── ui/
│   │       ├── CreatorDeskApp.kt            # Adaptive Navigation (Bar/Rail), TopBar & Dialoge
│   │       ├── components/
│   │       │   └── CreatorComponents.kt     # Karten, Badges, Status-Pills & Fortschrittsring
│   │       ├── screens/
│   │       │   ├── DashboardScreen.kt       # Studio-Dashboard & Produktions-Pipeline
│   │       │   ├── IdeasScreen.kt           # Ideenliste, Live-Suche & Filter-Chips
│   │       │   ├── HooksScreen.kt           # Hook-Studio mit Vorlagen & Sprechzeit-Anzeige
│   │       │   ├── CalendarScreen.kt        # Monatskalender & Tagesplanung
│   │       │   └── ContentEditorSheet.kt    # Modal-Editor mit DatePicker & freier Uhrzeit
│   │       └── theme/
│   │           ├── Color.kt                 # Helles & dunkles Studio-Farbschema
│   │           ├── Type.kt                  # Variable-Font-Typografie
│   │           └── Theme.kt                 # Material-3-Theme & Systemleisten-Kontrast
│   └── res/
│       ├── drawable/                        # Adaptive App-Icon-Layer & Studio-Banner
│       ├── font/                            # Lokale .ttf-Dateien (Space Grotesk, Plus Jakarta Sans)
│       ├── mipmap-*/                        # Benutzerdefinierte App-Icons für alle Dichten
│       └── values/strings.xml               # Deutsche UI-Texte & Barrierefreiheits-Labels
└── test/java/com/example/
    ├── ExampleUnitTest.kt                   # Unit-Tests für Kalender-, Zeitzonen- & Hook-Logik
    └── ExampleRobolectricTest.kt            # Robolectric- & In-Memory-Room-Repository-Tests
```

---

## Tests & Qualitätssicherung

Das Projekt enthält lokale JVM- und Robolectric-Tests:
- **Datums- & Zeitzonen-Tests** (`ExampleUnitTest.kt`): Schaltjahre, Epoch-Day-Konvertierung, lokale Zeitzonen-Umrechnung über Mitternacht, Monatswechsel und Hook-Wort-/Sprechzeit-Berechnung.
- **Room-Repository- & Ressourcen-Tests** (`ExampleRobolectricTest.kt`): In-Memory-Datenbanktests für das Speichern, Erhalten von `createdAtMillis` beim Bearbeiten, Setzen von `publishedAtEpochDay` beim Veröffentlichen und unveränderte Daten beim Anheften.

---

## Voraussetzungen & Build

### Voraussetzungen
- **Android Studio** (aktuelle Version mit Unterstützung für AGP 9 / Kotlin)
- **JDK 11** oder neuer
- **Android SDK 36** (`minSdk = 24`, `targetSdk = 36`)

### Projekt klonen, bauen und testen

```bash
# Repository klonen
git clone <REPOSITORY-URL>
cd creator-desk

# Debug-APK erstellen
./gradlew :app:assembleDebug

# Unit- und Robolectric-Tests ausführen
./gradlew :app:testDebugUnitTest
```

Alternativ kann das Projekt direkt in **Android Studio** geöffnet und über **Run** auf einem angeschlossenen Android-Smartphone oder Emulator gestartet werden.

---

## Datenschutz & lokale Datenspeicherung

- **100 % lokal auf dem Gerät**: Alle Content-Ideen, Hooks, Termine, Notizen und App-Einstellungen verbleiben ausschließlich lokal in der SQLite-/Room-Datenbank und im DataStore des Geräts.
- **Kein Benutzerkonto erforderlich**: Sofort nutzbar ohne Registrierung, Login oder externe Server.
- **Keine unnötigen Berechtigungen**: Die App benötigt weder Internet-, Kamera- noch Speicherzugriffsrechte.

---

## Mögliche Erweiterungen

- Erinnerungen / lokale Benachrichtigungen vor geplanten Veröffentlichungsterminen
- Export und Backup der Content-Datenbank als JSON oder CSV
- Checklisten pro Video-Produktion (z. B. *Skript*, *Dreh*, *Schnitt*, *Thumbnail*, *Beschreibung*)
- Homescreen-Widget für die nächsten anstehenden Veröffentlichungen

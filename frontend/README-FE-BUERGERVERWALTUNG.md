# Frontend für die Domäne Bürgerverwaltung

Die Bürgerverwaltung ist ein eigener Bounded Context im Backend und verwaltet ausschließlich Bürgerstammdaten wie Name, Email, Adresse und Wahlkreis.
Das Frontend stellt genau diese Backend-Funktionalitäten zur Verfügung.

Zusätzlich gibt es eine UI mit Mock-Login, also ohne Funktionalitäten. Dieser Login gehört nicht zur Bürgerverwaltungsdomäne und bildet keine Authentifizierung ab – er dient ausschließlich der Navigation in die UI.

## Dateistruktur (DDD-orientiert)

```
frontend/
└── buergerverwaltung/
├── index.html          # Mock-Login (nicht Teil der Domäne)
├── buerger.html        # Haupt-UI der Bürgerverwaltung
│
├── api/
│ └── voterApi.js       # Infrastruktur: REST-Calls an Voter-Endpunkte
│
├── ui/
│ ├── login.js          # Mock-Login Navigation
│ ├── buerger-create.js # Use Case: Bürger anlegen
│ ├── buerger-view.js   # Use Case: Bürger anzeigen
│ ├── buerger-utils.js  # (optional) UI-Helfer, z. B. Formatierungen
│ └── dom.js            # DOM-/Render-Helfer (Fehler, Erfolg, Text)
│
├── styles/
│ └── styles.css        # Styling
│
└── assets/
```

## Einordnung ins Gesamtsystem

Dieses Frontend adressiert ausschließlich die Domäne Bürgerverwaltung.
Daher enthält es nur die Funktionen:

- Bürger anlegen (POST /api/voter)
- Bürger anzeigen (GET /api/voter/{id})

Die Felder basieren direkt auf dem Domainmodell Voter.java:

- Name (Vorname, Nachname)
- Email
- Adresse (Straße, PLZ, Ort)
- Wahlkreis

Backend-generierte Felder wie 
- id
- registeredAt 
- isVerified

werden nur angezeigt

Der Login-Screen (index.html) ist ein Mock und dient nur als „Einstiegspunkt“ in das Frontend.
Er bildet keine Auth-Domain ab und ist technisch, nicht fachlich.

## Einordnung in DDD

Die Struktur ist so DDD-orientiert, also:
- jede Domäne erhält ihr eigenes Frontend-Modul
- keine Vermischung von Bürgerverwaltung und anderen Domänen

Außerdem gibt es eine klare Schichtentrennung:

- **ui/** = Application Layer (Use Cases der Domäne)
- **api/** = Infrastruktur (Kommunikation per REST)
- **.html + styles/** = Presentation Layer

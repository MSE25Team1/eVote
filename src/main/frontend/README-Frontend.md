# eVote Frontend (Update 29.11.25)

Dieses Frontend bildet den prototypischen Einstieg für Bürger:innen in das eVote-System.  
Es deckt aktuell zwei fachliche Bereiche ab:

- **Bürgerverwaltung** – die vorherige "große" Anzeige wurde reduziert, sodass die meisten Felder nur angezeigt werden können
- **Beispielabstimmung** – *„Campus-Kantinenkonzept 2026“* als Voting-Demo

Das Frontend wird über einen kleinen **Express-Webserver** ausgeliefert und nutzt **Bootstrap** für ein schlankes Layout.  
Die Authentifizierung ist zurzeit ein **Mock-Login** und bildet keine echte Auth-Domäne ab.

---

## Technologien

- **HTML/CSS/JavaScript**
- **Bootstrap 5**  
- **Node.js / Express**
---

## Dateistruktur

```text
src/
└── main/
    └── frontend/
        ├── assets/
        │   └── bootstrap.bundle.min.js     # Bootstrap JS (Bundle inkl. Popper)
        │
        ├── api/
        │   └── voterApi.js                 # REST-Calls zur Bürgerverwaltung
        │   # (weitere APIs wie pollQueryApi.js / voteCommandApi.js folgen)
        │
        ├── pages/
        │   ├── index.html                  # Startseite / Dashboard
        │   ├── buerger.html                # Profilseite
        │   └── voting001.html              # Beispielabstimmung „Campus-Kantinenkonzept 2026“
        │
        ├── server/
        │   ├── package.json                # Express-Konfiguration
        │   ├── package-lock.json
        │   ├── server.js                   # Node/Express-Server
        │   └── node_modules/
        │
        ├── styles/
        │   ├── bootstrap.min.css           # Bootstrap CSS
        │   └── styles.css                  # Projektspezifisches Styling
        │
        └── ui/
            ├── dom.js                      # DOM-/Render-Helfer
            ├── login.js                    # Mock-Login / Navigation
            ├── indexfunction.js            # Logik für Startseite
            ├── buerger-create.js           # Bürger anlegen (spätere Erweiterung)
            ├── buerger-view.js             # Bürger anzeigen / E-Mail aktualisieren
            └── abstimmung-vote.js          # UI-Logik für die Beispielabstimmung
```

## Seiten und Use Cases

`pages/index.html` Startseite / Dashboard: Begrüßung und Erklärung von eVote
- Profil-Teaser (Link zur Profilseite)
- Übersicht über Abstimmungen:
- Navbar mit Mock-Login („Max Mustermann“) und Logout-Link

`pages/buerger.html` Profil / Bürgerverwaltung 
- Anzeige der Bürgerstammdaten 
- Änderbar ist aktuell nur die Kontakt-E-Mail
- Speichern löst später REST-Call über voterApi.js aus 
- Zusätzlich: kleines Feedback-Formular („Fehler in deinen Daten entdeckt?“) – Demo ohne Backend-Logik

`pages/voting001.html` Beispielabstimmung „Campus-Kantinenkonzept 2026“

## Start des Frontends

**Voraussetzung:** Node.js installiert.

```
cd src/main/frontend/server  
npm install  
npm start
```

Der Express-Server läuft unter:

 **http://localhost:3000**

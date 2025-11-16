# Backend für die Domäne Bürgerverwaltung

Das Projekt wurde um ein Spring Boot Backend ergänzt, das die REST-Schnittstellen für die Domäne Bürgerverwaltung bereitstellt. Damit kann das Frontend (HTML/JS) echte Daten anlegen und abrufen.

## Über Spring Boot

Das bestehende Projekt enthielt nur Domain-Klassen (Voter, Name, Adresse, Email), aber keinen Webserver und keine REST-API.
Damit konnte das Frontend keine Bürgerdaten anlegen oder anzeigen. Spring Boot löst das, indem es:

- einen eingebauten Webserver (Tomcat) startet
- eine REST-Schnittstelle zur Verfügung stellt
- JSON-Requests des Frontends entgegennehmen kann
- die Domainlogik ausführt (Voter erzeugen, speichern, zurückgeben)

Das **Frontend läuft auf Port 3000**, das **Backend auf 8080**. Frontend <--> Backend Kommunikation:
```
POST http://localhost:8080/api/voter        (Bürger anlegen)
GET  http://localhost:8080/api/voter/{id}   (Bürger abrufen)
```

## neue Backend-Struktur

```
src/main/java/com/mse/eVote/
├── EvoteApplication.java           # Spring Boot Startklasse
│
└── buergerVerwaltung
├── domain                          # bereits vorhanden (unverändert)
│ ├── model/Voter.java
│ ├── repository/VoterRepository.java
│ ├── valueobjects/*
│ └── events/VoterRegisteredEvent.java
│
├── api                             # NEU: REST-Schicht für die UI
│ ├── VoterController.java          # Endpunkte /api/voter
│ ├── VoterCreateRequest.java       # DTO (Data Transfer Object) für POST-Requests
│ └── VoterResponse.java            # DTO für Antworten an das Frontend
│
├── service                         # NEU: Geschäftslogik
│ └── VoterService.java
│
└── repository
└── InMemoryVoterRepository.java    # NEU: Speicher im RAM

```


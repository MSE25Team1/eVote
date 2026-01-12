# eVote – Online-Abstimmungssystem

eVote ist ein Prototyp für eine digitale Plattform zur Durchführung von Bürgerbefragungen und Abstimmungen.
Das Projekt dient als Lern- und Referenzprojekt für saubere Softwarearchitektur, Testbarkeit und nachvollziehbare Domänenmodellierung.

Der Fokus liegt nicht auf einer produktiven Wahlanwendung, sondern auf Verständlichkeit, klarer Struktur und technischer Qualität.

## Ziel des Projekts

eVote zeigt, wie ein fachlich komplexes System strukturiert umgesetzt werden kann:

- Fachlogik steht im Mittelpunkt (Domain-Driven Design)
- Geschäftsregeln sind durch Tests abgesichert 
- Infrastruktur bleibt austauschbar 
- Frontend und Backend sind klar getrennt 
- Das Projekt ist nach Domain-Driven Design (DDD) aufgebaut. 
- Die Fachlichkeit ist in klar abgegrenzte Bounded Contexts unterteilt.

## Umgesetzte Domänen

### Bürgerverwaltung

Verwaltet Bürger (Voter), deren Stammdaten und Verifikationsstatus.
Hier entstehen Value Objects wie Name, Adresse und E-Mail, die für konsistente und valide Daten sorgen.

### Abstimmungsverwaltung

Verwaltet Abstimmungen (Polls) mit Laufzeit und Optionen.
Hier wird entschieden, ob eine Abstimmung aktiv, beendet oder gültig ist.

### Stimmvergabe
Ermöglicht die Abgabe von Stimmen.
Stimmen sind anonym und enthalten keine Referenz auf Bürger.
Die Regel „ein Bürger darf nur einmal abstimmen“ wird über den Zustand des Bürgers sichergestellt.


## Projektstruktur (Backend)

Jeder Bounded Context ist gleich aufgebaut:

- **domain**: Enthält die eigentliche Fachlogik: Aggregate, Value Objects, Domain Events und Repository-Interfaces.
- **application**: Enthält Services und Use Cases, die mehrere Domain-Objekte koordinieren.
- **infrastructure**: Technische Implementierungen wie REST-Controller und InMemory-Repositories.
- **events**: Domänenereignisse zur Entkopplung der Fachbereiche.

Zusätzlich existieren:

- **aspects**: für Querschnittsthemen wie Logging (AOP)
- **config**: für zentrale Konfigurationen und Demo-Daten (Seeder)

## Technologie-Stack

### Backend

- Java 25 – LTS-Version 
- Spring Boot – Webserver, REST-API und Dependency Injection
- Maven – Build, Tests und Reports 
- JUnit 5 & Mockito – Unit-Tests 
- JaCoCo – Testabdeckung mit Mindestanforderung 
- AspectJ – Logging und Cross-Cutting Concerns 
- InMemory-Persistenz –  ohne Datenbank

### Frontend

- HTML, CSS, JavaScript – bewusst ohne Framework 
- Bootstrap 5 – Layout und Basis-Styling 
- Node.js mit Express – lokaler Dev-Server 

Backend läuft auf Port **8080**. Frontend läuft auf Port **3000**. 

Das Frontend kommuniziert direkt mit den REST-Endpunkten des Backends, z. B. zum Anlegen von Bürgern oder zur Stimmabgabe.

## Tests & Qualität

- Test-Driven Development für zentrale Geschäftsregeln 
- Hohe Testabdeckung für Domain- und Application-Layer 
- Automatische Tests bei jedem Push 
- Mindestabdeckung wird technisch erzwungen

## CI/CD

- Automatischer Build und Test bei jedem Push 
- Veröffentlichung von Dokumentation und Testreports bei Änderungen auf dem main-Branch 
- Bereitstellung über GitHub Pages

## Projekt lokal starten

### Erstellen des JaCoCo Reports

**Voraussetzungen**:
- Java installiert (JDK 25)
- Maven installiert

```
# Maven Projektdokumentation generieren
mvn site
# JaCoCo Report generieren
mvn jacoco:report
```
alternativ:
```
start target/site/jacoco/index.html
# localhost:8000 im Browser aufrufen
```

### Frontend starten

```
cd src/main/frontend/server  
npm install
npm start

# Aufruf in Browser unter http://localhost:3000/
# Strg+C zum Beenden
```

### Backend starten
```
mvn spring-boot:run
# Strg+C zum Beenden
```

## Wiki
mehr zum Projekt inkl. Lösunden der Übungesaufgaben in der Wiki unter https://github.com/MSE25Team1/eVote/wiki 
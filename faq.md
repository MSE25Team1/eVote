# FAQ – eVote (Allgemein, Code, Wiki, Codebeispiele)

## Teil 1: Allgemein

### 1) Was ist eVote?
**Antwort:** eVote ist ein Prototyp für digitale Bürgerbefragungen und Abstimmungen. Der Fokus liegt auf verständlicher Fachlogik, klarer Architektur und Testbarkeit – nicht auf produktivem Wahleinsatz.

### 2) Was ist das Ziel des Projekts?
**Antwort:** Das Projekt soll zeigen, wie man ein fachlich komplexes System sauber strukturiert, testet und verständlich dokumentiert. Dabei stehen DDD, klare Schichten und nachvollziehbare Regeln im Mittelpunkt.

### 3) Welche Domänen deckt eVote ab?
**Antwort:**
- **Bürgerverwaltung:** Registrierung und Verifizierung von Wählern
- **Abstimmungsverwaltung:** Verwaltung von Abstimmungen (Polls) mit Zeitfenster
- **Stimmvergabe:** Abgabe von Stimmen und Validierung der Regeln

### 4) Was ist absichtlich vereinfacht?
**Antwort:**
- Keine echte Authentifizierung (Mock-Login)
- Keine persistente Datenbank (InMemory)
- Poll-Read-API nur als Frontend-Platzhalter

### 5) Für wen ist das Projekt gedacht?
**Antwort:** Für Studierende, Lehrende und Entwickler:innen, die saubere Architektur, DDD und Testbarkeit an einem verständlichen Beispiel sehen möchten.

### 6) Ist das System für echte Wahlen geeignet?
**Antwort:** Nein. Es handelt sich bewusst um einen Lern- und Demonstrationsprototypen.

### 7) Welche Regeln stehen fachlich im Vordergrund?
**Antwort:** Verifizierte Wähler dürfen abstimmen, Abstimmungen sind nur im definierten Zeitfenster offen, Double-Voting ist verhindert.

### 8) Wie werden Fachregeln abgesichert?
**Antwort:** Fachregeln werden im Domain-Modell und in Validatoren umgesetzt und durch Unit-Tests abgesichert.

### 9) Gibt es eine Trennung zwischen Fachlogik und Technik?
**Antwort:** Ja. Die Domäne enthält die Regeln, die Infrastruktur kümmert sich um REST, Logging und Persistenz.

### 10) Welche Daten sind sensibel?
**Antwort:** Wählerdaten wie Name, Adresse und E-Mail sind sensibel, werden aber im Prototyp nur im Speicher gehalten.

### 11) Welche Rollen gibt es im System?
**Antwort:** Im Prototyp werden keine Rollen oder Berechtigungen technisch erzwungen; es gibt nur fachliche Konzepte wie Voter und Poll.

### 12) Welche Sprache wird in der Domäne genutzt?
**Antwort:** Die Domäne nutzt Begriffe wie Voter, Poll und Vote, die in Code und Dokumentation konsistent verwendet werden.

### 13) Wie wird „ein Bürger, eine Stimme“ umgesetzt?
**Antwort:** Der Voter speichert, für welche Polls bereits abgestimmt wurde, und verhindert dadurch Double-Voting.

### 14) Wie wird die Gültigkeit einer Abstimmung geprüft?
**Antwort:** Eine Poll ist nur im Zeitfenster geöffnet (`startDate <= now < endDate`) und kann zusätzlich manuell geschlossen werden.

### 15) Welche Teile sind bewusst demohaft?
**Antwort:** Das Frontend ist minimalistisch, die Persistenz ist InMemory, und es gibt keine vollständige Read-API für Polls.

### 16) Gibt es ein Glossar?
**Antwort:** Begriffe werden in den einzelnen Antworten erklärt; ein separates Glossar ist nicht mehr notwendig.

### 17) Wo findet man Einstiegspunkte im Code?
**Antwort:** Bei `EvoteApplication.java` (Spring Boot Startpunkt) und in den Domain-Modellen der drei Bounded Contexts.

### 18) Wie wird mit Zeit gearbeitet?
**Antwort:** Zeitabhängige Logik basiert auf einer Clock, damit Tests deterministisch bleiben.

### 19) Was ist der fachliche Kern von eVote?
**Antwort:** Abstimmen unter klaren Regeln: nur verifizierte Wähler, nur im Zeitfenster, nur einmal.

### 20) Was unterscheidet eVote von einer Produktivlösung?
**Antwort:** Es fehlen Authentifizierung, echte Persistenz, Sicherheitsanforderungen und Skalierung.

## Teil 2: Code (Struktur, Techstack, Betrieb)

### 21) Welche Technologien werden im Backend genutzt?
**Antwort:** Java 17, Spring Boot, Maven, JUnit 5, Mockito, JaCoCo, AspectJ, Logback.

### 22) Welche Technologien werden im Frontend genutzt?
**Antwort:** HTML/CSS/JS, Bootstrap und ein Express-Dev-Server (Node.js).

### 23) Wie ist das Backend strukturiert?
**Antwort:** Jeder Bounded Context ist in **domain**, **application**, **infrastructure** und **events** gegliedert. Zusätzlich gibt es **aspects** und **config**.

### 24) Welche REST-Endpunkte gibt es?
**Antwort:**
- **/api/voter** (POST, GET, PUT)
- **/api/vote** (POST)

### 25) Wie starte ich das Projekt lokal?
**Antwort:**
- **Backend:** `mvn spring-boot:run` (Port 8080)
- **Frontend:** `cd src/main/frontend/server && npm install && npm start` (Port 3000)

### 26) Wie werden Demo-Daten erzeugt?
**Antwort:** Über Seeder, die beim Start automatisch ausgeführt werden.

### 27) Wie sieht Logging aus?
**Antwort:** AOP-Aspekte protokollieren Service-Aufrufe, Fehler und HTTP-Requests. Konfiguration liegt in `src/main/resources/logback-spring.xml`.

### 28) Wie funktioniert Fehlerbehandlung?
**Antwort:** Ein globaler Exception-Handler mappt `IllegalArgumentException` auf 400 und `IllegalStateException` auf 409.

### 29) Wie wird Testabdeckung gemessen?
**Antwort:** Mit JaCoCo. Reports werden per `mvn site` und `mvn jacoco:report` erzeugt.

### 30) Was macht die CI/CD-Pipeline?
**Antwort:** Bei jedem Push läuft `mvn verify`. Auf `main` werden zusätzlich Maven-Site und JaCoCo-Reports erzeugt und veröffentlicht.

### 31) Wie sind die Bounded Contexts im Code erkennbar?
**Antwort:** Jeder Kontext hat eigene Ordnerstruktur unter `src/main/java/evote/...`.

### 32) Wo liegen die Domain-Modelle?
**Antwort:** In `src/main/java/evote/<kontext>/domain/model`.

### 33) Wo liegen die Application Services?
**Antwort:** In `src/main/java/evote/<kontext>/application`.

### 34) Wo liegen die REST-Controller?
**Antwort:** In `src/main/java/evote/<kontext>/infrastructure/web`.

### 35) Wo liegen die Repository-Interfaces?
**Antwort:** In `src/main/java/evote/<kontext>/domain/repository`.

### 36) Wo liegen die InMemory-Repositories?
**Antwort:** In `src/main/java/evote/<kontext>/infrastructure/persistence`.

### 37) Wie wird ein Voter erstellt?
**Antwort:** Über `VoterService.create(...)` und den zugehörigen Controller-Endpunkt.

### 38) Wie wird eine Stimme abgegeben?
**Antwort:** Über `VoteService.create(...)`, der Poll, Voter und Vote validiert.

### 39) Wie wird verhindert, dass ein Voter doppelt abstimmt?
**Antwort:** Der Voter speichert Poll-IDs und `markVoted` verhindert Doppelabstimmungen.

### 40) Wie werden Poll-Optionen validiert?
**Antwort:** Poll prüft Optionen auf Null/Leer/Duplikate bereits im Konstruktor.

### 41) Wie werden Requests geloggt?
**Antwort:** Über `RequestLoggingAspect`, das alle Controller-Methoden protokolliert.

### 42) Wie werden Fehler geloggt?
**Antwort:** Über `LoggingAspect`, das Service-Fehler in `error.log` schreibt.

### 43) Wie ist CORS geregelt?
**Antwort:** Über eine zentrale Konfiguration, die Anfragen vom Frontend erlaubt.

### 44) Wie werden DTOs genutzt?
**Antwort:** DTOs kapseln API-Daten und werden in Assemblers in Domain-Objekte übersetzt.

### 45) Wie wird Zeit in Tests kontrolliert?
**Antwort:** Durch eine injizierte Clock, die in Tests auf eine feste Zeit gesetzt wird.

### 46) Welche Testarten sind vorhanden?
**Antwort:** Vor allem Unit-Tests für Domain-Logik und Application-Services.

### 47) Welche Dateien sind zentrale Einstiegspunkte für neue Entwickler?
**Antwort:** `README.md`, `EvoteApplication.java`, Domain-Modelle und `VoteService`.

### 48) Welche Pakete sind besonders wichtig?
**Antwort:** `evote.buergerverwaltung`, `evote.abstimmungsverwaltung`, `evote.stimmvergabe`.

### 49) Wie werden Domain Events genutzt?
**Antwort:** Events werden erzeugt, aber das Event-Handling ist noch nicht produktiv verdrahtet.

### 50) Wie sind Tests strukturiert?
**Antwort:** Analog zur Produktstruktur, z. B. `src/test/java/evote/abstimmungsverwaltung/domain/model`.

## Teil 3: Wiki-Themen (mit Bezug zum Projekt)

### 51) Git-Grundlagen – wie wird es hier genutzt?
**Antwort:** Standard-Git-Workflow mit Branches, Commits und PRs. Die Änderungen werden über PRs zusammengeführt.

### 52) Was sind typische Git-Artefakte im Projekt?
**Antwort:** `.gitignore` und die Workflow-Datei für CI/CD.

### 53) CI/CD-Pipeline – was passiert konkret?
**Antwort:** Der Workflow `test-and-publish.yml` baut und testet das Projekt. Auf `main` werden Dokumentation und Reports veröffentlicht.

### 54) Warum ist CI/CD wichtig?
**Antwort:** Damit Tests und Reports automatisch laufen und Qualität nicht von Hand geprüft werden muss.

### 55) UML & OOD (DDD) – wie zeigt sich das?
**Antwort:** Die Domäne ist in Bounded Contexts gegliedert. Aggregate wie `Voter` und `Poll` halten die Regeln der Fachlichkeit.

### 56) Was bedeutet DDD hier konkret?
**Antwort:** Fachbegriffe werden direkt im Code modelliert, und Regeln sind im Domain-Modell verankert.

### 57) TDD & LLM-gestütztes Entwickeln – wie zeigt sich das?
**Antwort:** Zentrale Regeln sind als Unit-Tests umgesetzt. Die Architektur unterstützt Tests durch klare Schichten und deterministische Logik.

### 58) Was ist das AAA-Pattern?
**Antwort:** Eine Teststruktur mit drei Schritten: Arrange, Act, Assert.

### 59) AAA-Pattern als Einzeiler im Projektkontext?
**Antwort:** `var poll = createDefaultPoll(); poll.close(); assertFalse(poll.isOpen());`

### 60) MET & Frontend – was ist relevant?
**Antwort:** Das Frontend ist bewusst minimalistisch, dient als Demonstrator und nutzt die REST-Endpunkte des Backends. Metriken wie Coverage werden über JaCoCo sichtbar.

### 61) Was sind übliche Code-Metriken?
**Antwort:** Coverage, cyclomatische Komplexität, Duplizierung, Anzahl Tests.

### 62) Was bedeutet cyclomatische Komplexität?
**Antwort:** Sie beschreibt die Anzahl möglicher Pfade in einer Methode.

### 63) Clean Code Development – Beispiel im Projekt?
**Antwort:** Validierung liegt in Value Objects/Validatoren statt in Controllern.

### 64) Projektweiterentwicklung & AOP – warum AOP?
**Antwort:** AOP trennt Querschnittsthemen (z. B. Logging) von der Fachlogik, um den Code klar zu halten.

### 65) Funktionale Programmierkonzepte – was davon gibt es hier?
**Antwort:** Java Records für Immutability und Validator-Komposition über Functional Interfaces. Kein reaktiver FRP-Stack.

### 66) Welche Stellen zeigen Immutability?
**Antwort:** Records wie `Vote` und Domain Events.

### 67) Welche Stellen zeigen Validator-Komposition?
**Antwort:** `CompositeVoteValidator` kombiniert mehrere Validatoren.

### 68) Was sind Anti-Corruption Layer im Projekt?
**Antwort:** Die Validator-Adapter schützen die Domäne vor Kopplung zwischen Kontexten.

### 69) Wie wird LLM-gestütztes Entwickeln gedacht?
**Antwort:** Als Unterstützung für Analyse, Tests und Entwurf – die Fachlogik bleibt im Code nachvollziehbar.

### 70) Was ist die Rolle der Wiki-Seiten?
**Antwort:** Die Wiki-Seiten geben konzeptionellen Kontext zu Git, CI/CD, DDD, TDD, MET, AOP und funktionalen Konzepten.

### 71) Wie helfen die Wiki-Themen beim Onboarding?
**Antwort:** Sie erklären Begriffe und Vorgehen, die im Code wiederzufinden sind.

### 72) Was sind typische Stolperstellen für neue Leser?
**Antwort:** Bounded Contexts, Validator-Ketten und Domain Events – diese sind zentral für das Verständnis.

### 73) Warum sind InMemory-Repositories sinnvoll im Prototyp?
**Antwort:** Sie halten das System leicht verständlich und vermeiden Datenbank-Komplexität.

### 74) Warum ist die Frontend-API bewusst klein?
**Antwort:** Der Fokus liegt auf der Fachlogik, nicht auf vollständiger UI.

### 75) Welche Rolle spielt JaCoCo im Wiki-Thema MET?
**Antwort:** JaCoCo liefert die Coverage-Metrik und macht Testabdeckung sichtbar.

## Teil 4: Codebeispiele und konkrete Stellen

### 76) Beispiel: Voter-Aggregat mit Regeln
**Antwort:** Das Voter-Objekt hält Verifikationsstatus und verhindert Double-Voting.
```java
public void markVoted(String pollId) {
    if (!isVerified) {
        throw new IllegalStateException("Voter muss verifikziert sein um abzustimmen");
    }
    if (hasVoted(pollId)) {
        throw new IllegalStateException("Voter hat bereits für diese Abstimmung abgestimmt: " + pollId);
    }
    votedPollIds.add(pollId);
}
```

### 77) Beispiel: Poll mit Zeitfenster
**Antwort:** `Poll` prüft, ob eine Abstimmung geöffnet ist und validiert Optionen.
```java
public boolean isOpen() {
    Instant instantNow = Instant.now(clock);
    return isOpenAt(instantNow);
}
```

### 78) Beispiel: Vote-Service als Use-Case
**Antwort:** `VoteService` koordiniert Voter, Poll und Vote für den Abstimmungsprozess.
```java
Poll poll = pollRepository.findById(req.pollId())
        .orElseThrow(() -> new IllegalArgumentException("Poll not found: " + req.pollId()));
Voter voter = voterRepository.findById(req.voterId())
        .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + req.voterId()));
```

### 79) Beispiel: Composite Validator
**Antwort:** Validierung wird über mehrere Validatoren kombiniert, z. B. Poll- und Voter-Validierung.
```java
default CompositeVoteValidator and(CompositeVoteValidator other) {
    return (vote, poll, voter) -> {
        this.validate(vote, poll, voter);
        other.validate(vote, poll, voter);
    };
}
```

### 80) Beispiel: AOP-Logging
**Antwort:** Service- und Request-Logging sind als Aspekte umgesetzt.
```java
@Before("execution(* evote.*.application.*Service.*(..))")
public void logBeforeServiceMethod(JoinPoint joinPoint) {
    defaultLogger.info("Aufruf von Service-Methode: {}.{}",
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName());
}
```

### 81) Beispiel: Regex im Code
**Antwort:** Regex wird z. B. bei der Validierung von Hausnummern genutzt.
```java
String houseNumberRegex = "^[\\d]+[a-zA-Z]?$";
if (!houseNumber.matches(houseNumberRegex))
    throw new IllegalArgumentException("Invalid house number format: '" + houseNumber + "'");
```

### 82) Beispiel: AAA-Pattern in Tests
**Antwort:** Tests sind typischerweise in Arrange–Act–Assert gegliedert.
```java
Poll poll = createDefaultPoll(); // Arrange
poll.close();                    // Act
assertFalse(poll.isOpen());      // Assert
```

### 83) Beispiel: Logging-Konfiguration
**Antwort:** Log-Dateien und Logger sind zentral konfiguriert.
```xml
<logger name="evote.errors" level="ERROR" additivity="false">
    <appender-ref ref="ERROR_FILE"/>
    <appender-ref ref="CONSOLE"/>
</logger>
```

### 84) Beispiel: REST-Controller für Voter
**Antwort:** `VoterController` stellt Endpunkte zum Anlegen und Aktualisieren bereit.
```java
@PostMapping
public VoterResponse create(@RequestBody @Valid VoterCreateRequest req) {
    return service.create(req);
}
```

### 85) Beispiel: REST-Controller für Votes
**Antwort:** `VoteController` stellt den Abstimmungs-Endpunkt bereit.
```java
@PostMapping
public ResponseEntity<Void> create(@RequestBody @Valid VoteCreateRequest req) {
    service.create(req);
    return ResponseEntity.created(null).build();
}
```

### 86) Beispiel: Globaler Exception-Handler
**Antwort:** Fehler werden zentral in HTTP-Statuscodes übersetzt.
```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Invalid input: " + ex.getMessage());
}
```

### 87) Beispiel: Domain Event für Voter
**Antwort:** `VoterRegisteredEvent` beschreibt eine erfolgreiche Verifikation.
```java
public record VoterRegisteredEvent(
        String voterId,
        String firstName,
        String lastName,
        String district,
        LocalDateTime registeredAt,
        String correlationId
) {}
```

### 88) Beispiel: Domain Event für Vote
**Antwort:** `VoteCastEvent` beschreibt eine abgegebene Stimme.
```java
public record VoteCastEvent(String voteId, String pollId, String optionId, Instant castAt) {
    public VoteCastEvent {
        if (voteId == null || pollId == null || optionId == null || castAt == null) {
            throw new IllegalArgumentException("All event fields must be non-null");
        }
    }
}
```

### 89) Beispiel: Poll-Validator
**Antwort:** Prüft, ob eine Poll offen ist.
```java
if (!poll.isOpen()) {
    throw new IllegalStateException("Poll is not open for voting. Poll ID: " + poll.getPollId());
}
```

### 90) Beispiel: Voter-Validator
**Antwort:** Prüft Verifikation und Double-Voting.
```java
if (!voter.isVerified()) {
    throw new IllegalStateException("Voter must be verified in order to vote. Voter ID: " + voter.getVoterId());
}
```

### 91) Beispiel: Vote-Option-Validator
**Antwort:** Prüft, ob eine Option zur Poll gehört.
```java
if (!poll.getOptions().contains(vote.getOptionId())) {
    throw new IllegalArgumentException("Option does not exist in poll");
}
```

### 92) Beispiel: InMemory-Voter-Repository
**Antwort:** Hält Voter im Speicher als Referenz.
```java
private final Map<String, Voter> store = new HashMap<>();
```

### 93) Beispiel: InMemory-Poll-Repository
**Antwort:** Hält Polls im Speicher als Referenz.
```java
private final Map<String, Poll> store = new HashMap<>();
```

### 94) Beispiel: InMemory-Vote-Repository
**Antwort:** Hält Votes im Speicher als Referenz.
```java
private final Map<String, Vote> store = new HashMap<>();
```

### 95) Beispiel: Seeder-Konfiguration
**Antwort:** Startet Voter- und Poll-Seeder beim App-Start.
```java
@Bean
public CommandLineRunner seedDatabase(VoterSeeder voterSeeder, PollSeeder pollSeeder) {
    return args -> {
        voterSeeder.seedVoters();
        pollSeeder.seedPolls();
    };
}
```

### 96) Beispiel: Voter-Assembler
**Antwort:** Übersetzt zwischen DTOs und Domain-Objekten.
```java
Name name = new Name(req.name().firstName(), req.name().lastName());
Adresse adresse = new Adresse(req.address().street(), req.address().houseNumber(), "", req.address().plz(), req.address().city());
```

### 97) Beispiel: Poll-Assembler
**Antwort:** Bereitet Poll-Daten für das Frontend auf.
```java
return new PollDTO(
        poll.getPollId(),
        poll.getTitle(),
        poll.getOptions(),
        poll.getStartDate(),
        poll.getEndDate()
);
```

### 98) Beispiel: DTOs für Voter
**Antwort:** `VoterCreateRequest` und `VoterResponse` definieren API-Daten.
```java
public record VoterCreateRequest(
        @JsonProperty("name") NameRequest name,
        @JsonProperty("address") AdresseRequest address,
        @JsonProperty("email") String email
) {}
```

### 99) Beispiel: DTO für Poll
**Antwort:** `PollDTO` transportiert Poll-Daten.
```java
public record PollDTO(
        String pollId,
        String title,
        List<String> options,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
```

### 100) Beispiel: DTO für Vote
**Antwort:** `VoteCreateRequest` definiert Eingabedaten für die Stimmabgabe.
```java
public record VoteCreateRequest(
        @JsonProperty("pollId") String pollId,
        @JsonProperty("optionId") String optionId,
        @JsonProperty("voterId") String voterId,
        @JsonProperty("correlationId") String correlationId
) {}
```

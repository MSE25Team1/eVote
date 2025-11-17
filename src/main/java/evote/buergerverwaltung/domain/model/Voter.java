package evote.buergerverwaltung.domain.model;

import evote.buergerverwaltung.events.VoterRegisteredEvent;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.domain.valueobjects.Email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Voter - Aggregate Root der Bürgerverwaltung
 *
 * Repräsentiert einen berechtigten Wähler im eVote-System.
 *
 * Aggregate Invarianten:
 * 1. Voter kann nur abstimmen, wenn isVerified == true
 * 2. Voter kann pro Abstimmung (Poll) nur einmal abstimmen
 * 3. Email muss gültig sein
 * 4. Name, Adresse und Geburtsdatum sind unveränderbar nach Erstellung
 *
 * Verantwortlichkeiten:
 * - Verwaltung von Wahlberechtigung und Verifikationsstatus
 * - Tracking, an welchen Abstimmungen dieser Voter bereits teilgenommen hat
 * - Publishing von Domain Events (VoterRegisteredEvent)
 *
 * Als Aggregate Root hat diese Klasse:
 * - Eindeutige Identität (voterId)
 * - Alle Zugriffe von außen gehen durch diese Klasse
 * - Repository sollte nur dieses Objekt persistent speichern
 */
public class Voter {
    private final String voterId;
    private final Name name;
    private final Adresse adresse;
    private final Email email;
    private final LocalDate geburtsdatum;
    private final String wahlkreis;
    private boolean isVerified;
    private LocalDateTime registeredAt;
    private final Set<String> votedPollIds; // pollId -> bereits abgestimmt
    private VoterRegisteredEvent pendingEvent;

    /**
     * Private Konstruktor - Voter wird nur über factory methods erstellt
     */
    private Voter(
            String voterId,
            Name name,
            Adresse adresse,
            Email email,
            LocalDate geburtsdatum,
            String wahlkreis) {

        this.voterId = voterId;
        this.name = name;
        this.adresse = adresse;
        this.email = email;
        this.geburtsdatum = geburtsdatum;
        this.wahlkreis = wahlkreis;
        this.isVerified = false;
        this.registeredAt = null;
        this.votedPollIds = new HashSet<>();
        this.pendingEvent = null;
    }

    /**
     * Factory Method: Neue Voter-Registrierung
     * Setzt alle unveränderlichen Felder und initialisiert den Voter als unverifiziert.
     */
    public static Voter register(
            Name name,
            Adresse adresse,
            Email email,
            LocalDate geburtsdatum,
            String wahlkreis) {
        if (name == null)
            throw new IllegalArgumentException("Name darf nicht null sein.");
        if (adresse == null)
            throw new IllegalArgumentException("Adresse darf nicht null sein.");

        String voterId = UUID.randomUUID().toString();
        return new Voter(voterId, name, adresse, email, geburtsdatum, wahlkreis);
    }

    /**
     * Reconstruct from Persistence
     * (wird von Repository verwendet)
     */
    public static Voter reconstruct(
            String voterId,
            Name name,
            Adresse adresse,
            Email email,
            LocalDate geburtsdatum,
            String wahlkreis,
            boolean isVerified,
            LocalDateTime registeredAt,
            Set<String> votedPollIds) {
        Voter voter = new Voter(voterId, name, adresse, email, geburtsdatum, wahlkreis);
        voter.isVerified = isVerified;
        voter.registeredAt = registeredAt;
        voter.votedPollIds.addAll(votedPollIds);
        return voter;
    }

    /**
     * Verifikation des Voters durchführen
     * Nach erfolgreicher Verifikation ist der Voter berechtigt zu wählen.
     *
     * Erzeugt einen VoterRegisteredEvent (Domain Event).
     *
     * @throws IllegalStateException wenn bereits verifiziert
     */
    public void verify() {
        if (isVerified) {
            throw new IllegalStateException("Voter ist bereits verifiziert");
        }

        this.isVerified = true;
        this.registeredAt = LocalDateTime.now();

        // Domain Event erstellen (wird später vom Repository publiziert)
        this.pendingEvent = new VoterRegisteredEvent(
            voterId,
            name.getFirstName(),
            name.getLastName(),
            wahlkreis,
            registeredAt,
            UUID.randomUUID().toString() // correlationId
        );
    }

    /**
     * Markiert, dass dieser Voter an einer bestimmten Abstimmung teilgenommen hat.
     * Verhindert Double-Voting pro Poll.
     *
     * Invariante-Prüfung: Voter muss verifikziert sein.
     *
     * @param pollId Die ID der Abstimmung
     * @throws IllegalStateException wenn Voter nicht verifikziert oder bereits abgestimmt
     */
    public void markVoted(String pollId) {
        if (!isVerified) {
            throw new IllegalStateException("Voter muss verifikziert sein um abzustimmen");
        }

        if (hasVoted(pollId)) {
            throw new IllegalStateException("Voter hat bereits für diese Abstimmung abgestimmt: " + pollId);
        }

        votedPollIds.add(pollId);
    }

    /**
     * Prüft, ob dieser Voter bereits für eine Abstimmung abgestimmt hat.
     *
     * @param pollId Die ID der Abstimmung
     * @return true wenn Voter bereits abgestimmt hat
     */
    public boolean hasVoted(String pollId) {
        return votedPollIds.contains(pollId);
    }

    /**
     * Holt das gepufferte Domain Event, falls vorhanden.
     * (wird vom Repository nach dem Speichern aufgerufen und geleert)
     */
    public VoterRegisteredEvent getPendingEvent() {
        return pendingEvent;
    }

    public void clearPendingEvent() {
        this.pendingEvent = null;
    }

    // Getters
    public String getVoterId() { return voterId; }
    public Name getName() { return name; }
    public Adresse getAdresse() { return adresse; }
    public Email getEmail() { return email; }
    public LocalDate getGeburtsdatum() { return geburtsdatum; }
    public String getWahlkreis() { return wahlkreis; }
    public boolean isVerified() { return isVerified; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public Set<String> getVotedPollIds() { return new HashSet<>(votedPollIds); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voter)) return false;
        Voter voter = (Voter) o;
        return voterId.equals(voter.voterId); // Identität basiert auf voterId
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId);
    }

    @Override
    public String toString() {
        return "Voter{" +
                "voterId='" + voterId + '\'' +
                ", name=" + name +
                ", wahlkreis='" + wahlkreis + '\'' +
                ", isVerified=" + isVerified +
                ", votedPolls=" + votedPollIds.size() +
                '}';
    }
}


package com.mse.eVote.buergerVerwaltung.domain.model;

import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Adresse; import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Name;

import java.time.LocalDate; import java.util.Objects;

public class Buerger { private final Name name; private final Adresse adresse; private final String email; private final LocalDate geburtsdatum; private final boolean isValidated; private final boolean teilgenommeneAbstimmung;

    public Buerger(Name name, Adresse adresse, String email, LocalDate geburtsdatum, boolean isValidated, boolean teilgenommeneAbstimmung) {
        if (name == null)
            throw new IllegalArgumentException("Name darf nicht null sein.");
        if (adresse == null)
            throw new IllegalArgumentException("Adresse darf nicht null sein.");
        if (email == null || !email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Ungültige E-Mail-Adresse: " + email);
        if (geburtsdatum == null)
            throw new IllegalArgumentException("Geburtsdatum darf nicht null sein.");

        this.name = name;
        this.adresse = adresse;
        this.email = email;
        this.geburtsdatum = geburtsdatum;
        this.isValidated = isValidated;
        this.teilgenommeneAbstimmung = teilgenommeneAbstimmung;
    }

    public Name getName() { return name; }
    public Adresse getAdresse() { return adresse; }
    public String getEmail() { return email; }
    public LocalDate getGeburtsdatum() { return geburtsdatum; }
    public boolean isValidated() { return isValidated; }
    public boolean hasTeilgenommeneAbstimmung() { return teilgenommeneAbstimmung; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Buerger)) return false;
        Buerger buerger = (Buerger) o;
        return isValidated == buerger.isValidated &&
                teilgenommeneAbstimmung == buerger.teilgenommeneAbstimmung &&
                name.equals(buerger.name) &&
                adresse.equals(buerger.adresse) &&
                email.equals(buerger.email) &&
                geburtsdatum.equals(buerger.geburtsdatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, adresse, email, geburtsdatum, isValidated, teilgenommeneAbstimmung);
    }

}
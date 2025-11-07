package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import java.util.Objects;

public class Name { private final String vorname; private final String nachname;

    public Name(String vorname, String nachname) {
        if (vorname == null || vorname.trim().isEmpty())
            throw new IllegalArgumentException("Vorname darf nicht null oder leer sein.");
        if (nachname == null || nachname.trim().isEmpty())
            throw new IllegalArgumentException("Nachname darf nicht null oder leer sein.");

        String regex = "^[A-ZÄÖÜ][a-zA-ZäöüßÄÖÜ\\-]*$";
        if (!vorname.matches(regex))
            throw new IllegalArgumentException("Ungültiger Vorname: " + vorname);
        if (!nachname.matches(regex))
            throw new IllegalArgumentException("Ungültiger Nachname: " + nachname);

        this.vorname = vorname;
        this.nachname = nachname;
    }

    public String getVorname() { return vorname; }
    public String getNachname() { return nachname; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;
        Name name = (Name) o;
        return vorname.equals(name.vorname) && nachname.equals(name.nachname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vorname, nachname);
    }

    @Override
    public String toString() {
        return vorname + " " + nachname;
    }

}
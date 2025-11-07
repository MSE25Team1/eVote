package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import java.util.Objects;

public class Adresse { private final String strasse; private final String hausnummer; private final String adresszusatz; private final String plz; private final String stadt;

    public Adresse(String strasse, String hausnummer, String adresszusatz, String plz, String stadt) {
        if (strasse == null || strasse.trim().isEmpty())
            throw new IllegalArgumentException("Straße darf nicht null oder leer sein.");
        if (hausnummer == null || hausnummer.trim().isEmpty())
            throw new IllegalArgumentException("Hausnummer darf nicht null oder leer sein.");
        if (plz == null || !plz.matches("^[0-9]{5}$"))
            throw new IllegalArgumentException("PLZ muss genau 5 Ziffern enthalten.");
        if (stadt == null || stadt.trim().isEmpty())
            throw new IllegalArgumentException("Stadt darf nicht null oder leer sein.");

        String strasseRegex = "^[A-Za-zÄÖÜäöüß0-9\\-\\. ]+$";
        String hausnummerRegex = "^[0-9]+[a-zA-Z]?$";
        String stadtRegex = "^[A-Za-zÄÖÜäöüß\\- ]+$";

        if (!strasse.matches(strasseRegex))
            throw new IllegalArgumentException("Ungültige Straße: " + strasse);
        if (!hausnummer.matches(hausnummerRegex))
            throw new IllegalArgumentException("Ungültige Hausnummer: " + hausnummer);
        if (!stadt.matches(stadtRegex))
            throw new IllegalArgumentException("Ungültige Stadt: " + stadt);

        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.adresszusatz = adresszusatz == null ? "" : adresszusatz;
        this.plz = plz;
        this.stadt = stadt;
    }

    public String getStrasse() { return strasse; }
    public String getHausnummer() { return hausnummer; }
    public String getAdresszusatz() { return adresszusatz; }
    public String getPlz() { return plz; }
    public String getStadt() { return stadt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adresse)) return false;
        Adresse adresse = (Adresse) o;
        return strasse.equals(adresse.strasse) &&
                hausnummer.equals(adresse.hausnummer) &&
                adresszusatz.equals(adresse.adresszusatz) &&
                plz.equals(adresse.plz) &&
                stadt.equals(adresse.stadt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strasse, hausnummer, adresszusatz, plz, stadt);
    }

    @Override
    public String toString() {
        return strasse + " " + hausnummer + (adresszusatz.isEmpty() ? "" : ", " + adresszusatz) + ", " + plz + " " + stadt;
    }

}
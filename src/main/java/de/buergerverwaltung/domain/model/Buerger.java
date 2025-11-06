package de.buergerverwaltung.domain.model;

public class Buerger {
    private final String vorname;
    private final String nachname;
    private final String email;
    private final String plz;

    public Buerger(String vorname, String nachname, String email, String plz) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.plz = plz;
    }

    public String getVorname() { return vorname; }
    public String getNachname() { return nachname; }
    public String getEmail() { return email; }
    public String getPlz() { return plz; }
}

package com.mse.eVote.buergerVerwaltung.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VoterCreateRequest {

    @JsonProperty("name")
    public NameRequest name;

    public String email;

    @JsonProperty("adresse")
    public AdresseRequest adresse;

    public String wahlkreis;

    // Nested class for name
    public static class NameRequest {
        public String vorname;
        public String nachname;
    }

    // Nested class for adresse
    public static class AdresseRequest {
        public String strasse;
        public String plz;
        public String ort;
    }
}

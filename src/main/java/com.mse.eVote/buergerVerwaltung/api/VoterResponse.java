package com.mse.eVote.buergerVerwaltung.api;

public class VoterResponse {
    public String id;
    public NameDTO name;
    public String email;
    public AddressDTO address;
    public String district;       // previously: wahlkreis
    public String registeredAt;
    public boolean verified;      // previously: isVerified

    public static class NameDTO {
        public String firstName;
        public String lastName;
        public String fullName;
    }

    public static class AddressDTO {
        public String street;       // Straße (ohne Hausnummer)
        public String houseNumber;  // Hausnummer
        public String postalCode;   // PLZ
        public String city;         // Ort
        public String formatted;    // full formatted address for UI
    }
}

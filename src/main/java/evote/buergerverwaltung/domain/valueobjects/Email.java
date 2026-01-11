package evote.buergerverwaltung.domain.valueobjects;

/**
 * Value Object für validierte E-Mail-Adressen im Bürgerkontext.
 */
public record Email(String value) {

    public Email {

        if (value == null || !value.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Ungültige E-Mail-Adresse: " + value);
        }
    }
    @Override
    public String toString() {
        return value;
    }
}

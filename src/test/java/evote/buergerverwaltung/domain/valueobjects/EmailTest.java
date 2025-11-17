package evote.buergerverwaltung.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email - Value Object Tests")
class EmailTest {

    // ============ Happy Path Tests ============

    @Test
    @DisplayName("Valid email should create successfully")
    void validEmail_shouldCreateEmailObject() {
        // Act & Assert
        assertDoesNotThrow(() -> new Email("max@mustermann.de"));
    }

    @Test
    @DisplayName("Email with subdomain should create successfully")
    void emailWithSubdomain_shouldCreateEmailObject() {
        // Act & Assert
        assertDoesNotThrow(() -> new Email("user@mail.example.com"));
    }

    @Test
    @DisplayName("Email with dots in username should create successfully")
    void emailWithDotsInUsername_shouldCreateEmailObject() {
        // Act & Assert
        assertDoesNotThrow(() -> new Email("first.last@example.com"));
    }

    @Test
    @DisplayName("Email with hyphens should create successfully")
    void emailWithHyphens_shouldCreateEmailObject() {
        // Act & Assert
        assertDoesNotThrow(() -> new Email("user-name@my-domain.com"));
    }

    // ============ Invalid Email Format Tests ============

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@domain.de", "", " "})
    @DisplayName("Invalid email formats should throw exception")
    void invalidEmail_shouldThrowException(String invalidEmail) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    @DisplayName("Null email should throw exception")
    void nullEmail_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    @DisplayName("Email without @ symbol should throw exception")
    void emailWithoutAtSymbol_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email("invalidemail.com"));
    }

    @Test
    @DisplayName("Email without domain extension should throw exception")
    void emailWithoutDomainExtension_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email("user@domain"));
    }

    @Test
    @DisplayName("Email with only @ should throw exception")
    void emailWithOnlyAt_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email("@"));
    }

    @Test
    @DisplayName("Email with spaces should throw exception")
    void emailWithSpaces_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Email("user name@example.com"));
    }

    // ============ Value Object Semantics Tests ============

    @Test
    @DisplayName("Two emails with same value should be equal")
    void identicalEmails_shouldBeEqual() {
        // Arrange & Act
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        // Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("Two emails with different values should not be equal")
    void differentEmails_shouldNotBeEqual() {
        // Arrange & Act
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("other@example.com");

        // Assert
        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("toString should return email value")
    void toStringFormat_shouldReturnEmailValue() {
        // Arrange & Act
        Email email = new Email("test@example.com");

        // Assert
        assertEquals("test@example.com", email.toString());
    }

    @Test
    @DisplayName("value accessor should return email value")
    void valueAccessor_shouldReturnEmailValue() {
        // Arrange & Act
        Email email = new Email("test@example.com");

        // Assert
        assertEquals("test@example.com", email.value());
    }
}


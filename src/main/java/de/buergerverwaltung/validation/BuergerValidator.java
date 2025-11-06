package de.buergerverwaltung.validation;

import de.buergerverwaltung.domain.model.Buerger;
import java.util.regex.Pattern;

public class BuergerValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PLZ_PATTERN = Pattern.compile("^\\d{5}$");

    public boolean isValid(Buerger b) {
        if (b == null) return false;
        return b.getVorname() != null && !b.getVorname().isBlank()
                && b.getNachname() != null && !b.getNachname().isBlank()
                && EMAIL_PATTERN.matcher(b.getEmail()).matches()
                && PLZ_PATTERN.matcher(b.getPlz()).matches();
    }
}

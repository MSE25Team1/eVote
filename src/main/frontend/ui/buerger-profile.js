import {getVoterById, updateVoterEmail} from "../api/voterApi.js";
import {renderError, renderSuccess} from "./dom.js";

const DEFAULT_VOTER_ID = "VOTER-001";
const OUTPUT_ID = "createOutput";

const fillProfileForm = (voter) => {
  document.getElementById("vorname").value = voter.name?.firstName ?? "";
  document.getElementById("nachname").value = voter.name?.lastName ?? "";
  document.getElementById("email").value = voter.email ?? "";

  const street = voter.address?.street ?? "";
  const houseNumber = voter.address?.houseNumber ?? "";
  document.getElementById("strasse").value = [street, houseNumber].filter(Boolean).join(" ");
  document.getElementById("plz").value = voter.address?.postalCode ?? "";
  document.getElementById("ort").value = voter.address?.city ?? "";
  document.getElementById("wahlkreis").value = voter.district ?? "";

  const navbarName = document.getElementById("navbarVoterName");
  if (navbarName && voter.name?.fullName) {
    navbarName.textContent = voter.name.fullName;
  }
};

const loadVoterProfile = async () => {
  try {
    const voter = await getVoterById(DEFAULT_VOTER_ID);
    fillProfileForm(voter);
  } catch (error) {
    renderError(OUTPUT_ID, error.message || "Konnte die Bürgerdaten nicht laden.");
  }
};

const updateEmail = async () => {
  const emailValue = document.getElementById("email").value.trim();
  if (!emailValue) {
    renderError(OUTPUT_ID, "Bitte gib eine gültige E-Mail-Adresse ein.");
    return;
  }

  try {
    const updatedVoter = await updateVoterEmail(DEFAULT_VOTER_ID, emailValue);
    fillProfileForm(updatedVoter);
    renderSuccess(OUTPUT_ID, "E-Mail wurde erfolgreich aktualisiert.");
  } catch (error) {
    renderError(OUTPUT_ID, error.message || "Fehler beim Speichern der Änderung.");
  }
};

document.addEventListener("DOMContentLoaded", () => {
  loadVoterProfile();
  document.getElementById("createBtn").addEventListener("click", updateEmail);
});

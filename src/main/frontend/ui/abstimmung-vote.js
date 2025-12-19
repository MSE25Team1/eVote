import { castVote } from "../api/voteCommandApi.js";

const form = document.getElementById("voteForm");
const output = document.getElementById("voteOutput");

const submitBtn = document.getElementById("submitVoteBtn");
const abstentionBtn = document.getElementById("enthaltungBtn");

// Demo-Werte (später z. B. aus Login / Session)
const POLL_ID = "POLL-CK-2026";
const VOTER_ID = "VOTER-001";

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const selectedOption = document.querySelector('input[name="option"]:checked');
    if (!selectedOption) {
        output.innerHTML = `
      <div class="alert alert-warning">
        Bitte wähle eine Option aus.
      </div>`;
        return;
    }

    const voteRequest = {
        pollId: POLL_ID,
        optionId: selectedOption.value,
        voterId: VOTER_ID,
        correlationId: crypto.randomUUID()
    };

    try {
        await castVote(voteRequest);

        // Buttons & Optionen ausblenden
        form.querySelector("#options-group").style.display = "none";
        submitBtn.style.display = "none";
        abstentionBtn.style.display = "none";

        output.innerHTML = `
      <div class="alert alert-success">
         <strong>Stimme erfolgreich abgegeben!</strong><br>
      </div>`;
    } catch (error) {
        output.innerHTML = `
      <div class="alert alert-danger">
         ${error.message}
      </div>`;
    }
});



const BASE_URL = "http://localhost:8080/api/vote";

/**
 * Sendet eine Stimme an das Backend.
 * @param {Object} voteData
 * @returns {Promise<boolean>} true bei Erfolg
 */
export async function castVote(voteData) {
    const response = await fetch(BASE_URL, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(voteData)
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Fehler bei der Stimmabgabe");
    }

    // Backend antwortet mit 201 Created, ohne Body
    return true;
}

const BASE_URL = "http://localhost:8080/api/voter";

export async function createVoter(voterData){
  return fetch(BASE_URL,{
    method:"POST",
    headers:{"Content-Type":"application/json"},
    body:JSON.stringify(voterData)
  }).then(r=>r.json());
}

export async function getVoterById(id){
  const r=await fetch(`${BASE_URL}/${id}`);
  if(!r.ok){
    const message = await r.text();
    throw new Error(message || "Fehler beim Laden der Bürgerdaten");
  }
  return await r.json();
}

export async function updateVoterEmail(id, email){
  const response = await fetch(`${BASE_URL}/${id}`,{ 
    method:"PUT",
    headers:{"Content-Type":"application/json"},
    body:JSON.stringify({email})
  });

  if(!response.ok){
    const message = await response.text();
    throw new Error(message || "Fehler beim Aktualisieren der E-Mail-Adresse");
  }

  return await response.json();
}
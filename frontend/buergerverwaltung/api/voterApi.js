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
  if(!r.ok) return null;
  return await r.json();
}
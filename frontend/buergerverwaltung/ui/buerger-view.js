import {getVoterById} from "../api/voterApi.js";
import {renderError} from "./dom.js";

document.getElementById("viewBtn").addEventListener("click", async ()=>{
  const id = document.getElementById("voterId").value.trim();
  const out=document.getElementById("viewOutput");
  if(!id){ renderError("viewOutput","Bitte ID eingeben"); return; }
  const v=await getVoterById(id);
  if(!v || !v.id){ renderError("viewOutput","Nicht gefunden"); return;}
  out.innerHTML=`ID: ${v.id}<br>
  Name: ${v.name?.fullName ?? (v.name?.firstName + " " + v.name?.lastName)}<br>
  Email: ${v.email}<br>
  Adresse: ${v.address?.formatted ?? "-"}<br>
  District: ${v.district}<br>
  Verified: ${v.verified ? "Yes" : "No"}<br>
  Registered at: ${v.registeredAt ?? "-"}<br>
  `;
});
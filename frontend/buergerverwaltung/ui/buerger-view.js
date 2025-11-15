import {getVoterById} from "../api/voterApi.js";
import {renderError} from "./dom.js";

document.getElementById("viewBtn").addEventListener("click", async ()=>{
  const id=voterId.value;
  const out=document.getElementById("viewOutput");
  const v=await getVoterById(id);
  if(!v){ renderError("viewOutput","Nicht gefunden"); return;}
  out.innerHTML=`ID: ${v.voterId}<br>
  Name: ${v.name.vorname} ${v.name.nachname}<br>
  Email: ${v.email}<br>
  Adresse: ${v.adresse.strasse}, ${v.adresse.plz} ${v.adresse.ort}<br>
  Wahlkreis: ${v.wahlkreis}<br>
  `;
});
import {createVoter} from "../api/voterApi.js";
import {renderSuccess,renderError} from "./dom.js";

document.getElementById("createBtn").addEventListener("click", async ()=>{
  const d={
    name:{vorname:vorname.value, nachname:nachname.value},
    email: email.value,
    adresse:{strasse:strasse.value, plz:plz.value, ort:ort.value},
    wahlkreis: wahlkreis.value
  };
  try{
    await createVoter(d);
    renderSuccess("createOutput","Bürger angelegt.");
  }catch(e){
    renderError("createOutput","Fehler.");
  }
});
export function renderSuccess(id,msg){
 const el=document.getElementById(id); el.style.color="green"; el.innerText=msg;
}
export function renderError(id,msg){
 const el=document.getElementById(id); el.style.color="red"; el.innerText=msg;
}
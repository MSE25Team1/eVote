import express from "express";
import path from "path";
import { fileURLToPath } from "url";

const __filename=fileURLToPath(import.meta.url);
const __dirname=path.dirname(__filename);

const app=express();
const FRONT=path.join(__dirname,"../buergerverwaltung");
app.use(express.static(FRONT));
app.get("/",(req,res)=>res.sendFile(path.join(FRONT,"index.html")));
app.listen(3000,()=>console.log("http://localhost:3000"));

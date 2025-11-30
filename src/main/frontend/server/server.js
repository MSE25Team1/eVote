import express from "express";
import path from "path";
import { fileURLToPath } from "url";

const __filename=fileURLToPath(import.meta.url);
const __dirname=path.dirname(__filename);

const app = express();
const FRONT = path.join(__dirname, "../");
const PAGES = path.join(FRONT, "pages");

// 1. HTML-Seiten aus /pages
app.use(express.static(PAGES));
// 2. Rest (styles, ui, assets, api, …) aus /frontend
app.use(express.static(FRONT));

app.get("/", (req, res) =>
  res.sendFile(path.join(PAGES, "index.html"))
);

app.listen(3000, () => console.log("http://localhost:3000"));

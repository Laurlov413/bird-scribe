import "dotenv/config";
import express from "express";
import cors from "cors";
import transcribeRouter from "./routes/transcribe.js";
import extractRouter from "./routes/extract.js";

const app = express();
app.use(cors());
app.use(express.json());
app.get("/health", (req,res) => res.json({ status: "ok"}));

app.use("/transcribe", transcribeRouter);
app.use("/extract", extractRouter);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`BirdScribe server listening on port ${PORT}`);
});
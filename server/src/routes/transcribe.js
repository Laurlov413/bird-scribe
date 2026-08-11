import { Router } from "express";
import multer from "multer";
import OpenAI from "openai";
import fs from "fs";
import os from "os";
import path from "path";

const router = Router();
const upload = multer({ storage: multer.memoryStorage() });
const openai = new OpenAI();

router.post("/", upload.single("audio"), async (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: "no audio file provided"});
    }

    const tempPath = path.join(os.tmpdir(), `birdscribe_${Date.now()}.m4a`);

    try {
        fs.writeFileSync(tempPath, req.file.buffer);

        const transcription = await openai.audio.transcriptions.create({
            file: fs.createReadStream(tempPath),
            model: "whisper-1",
        });

        res.json({ transcript: transcription.text });
    }   catch (err) {
        console.error("Transcription failed:", err);
        res.status(500).json({ error: "Transcription failed" });
    }   finally {
        fs.unlink(tempPath, () => {});
    }
});

export default router;

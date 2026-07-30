import { Router } from "express";
import multer from "multer";

const router = Router();
const upload = multer({ storage: multer.memoryStorage() });

router.post("/", upload.single("audio"), async (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: "no audio file provided"});
    }

    res.json({
        transcript: "TODO: replace with real Whisper transcription",
    });
});

export default router;

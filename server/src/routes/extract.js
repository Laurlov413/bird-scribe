import { Router } from "express";

const router = Router();

router.post("/", async (req, res) => {
    const { transcript } = req.body;

    if (!transcript) {
        return res.status(400).json({ error: "No transcript provided" });
    }
    res.json({
        checklist: [
            {
                species: "TODO",
                count: 0,
                notes: "replace with real Claude extraction",
                confidence: 0,
            },
        ],
    });
});

export default router;
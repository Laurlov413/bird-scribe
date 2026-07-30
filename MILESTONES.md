# BirdScribe — Milestones

- [x] **M0 — Repo scaffold**: monorepo structure, README, project spec,
      `.gitignore`, license, backend skeleton
- [ ] **M1 — Audio capture**: tap-to-record button in the Android app
      (interim trigger), save audio file locally, playback to confirm it
      works
- [ ] **M2 — Backend + transcription**: `/transcribe` endpoint accepts audio,
      calls Whisper, returns transcript
- [ ] **M3 — AI extraction agent**: `/extract` endpoint takes a transcript,
      calls Claude, returns structured bird checklist JSON
- [ ] **M4 — Checklist UI**: list view showing the parsed checklist, editable,
      saved to Room
- [ ] **M5 — Polish**: offline queuing (record now, upload when signal
      returns), CSV export, basic error states
- [ ] **M6 — Wake-word trigger (core goal)**: replace tap-to-record with
      always-listening wake-word detection (Picovoice Porcupine) for true
      hands-free operation

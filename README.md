# BirdScribe 🐦

A hands-free field companion for birders. Say the word, narrate what you see
and hear while your hands stay on your binoculars, and BirdScribe transcribes
the audio and uses an AI agent to extract a structured checklist of species
sighted — no manual data entry in the field.

## Vision

The goal is fully hands-free operation: a wake phrase starts recording
automatically, so you never have to fumble with your phone mid-sighting.
The app is being built incrementally toward that — starting with a
tap-to-record trigger to get the core pipeline (recording → transcription →
AI extraction) solid and reliable, then replacing the trigger with
always-listening wake-word detection once the rest of the app works.

## How it works

1. **Record** — hands-free wake-word activation (in progress — currently a
   tap-to-record button while the core pipeline is being built out).
2. **Transcribe** — the recording is sent to the backend, which transcribes it
   with Whisper.
3. **Extract** — the transcript is sent to Claude, which pulls out species,
   counts, and notes as structured JSON.
4. **Review** — the checklist appears in the app, editable and saved locally
   (Room), with CSV/eBird-format export.

See [`PROJECT.md`](./PROJECT.md) for the full project spec and
[`MILESTONES.md`](./MILESTONES.md) for the build plan.

## Repo structure

\`\`\`
bird-scribe/
├── app/       Android Studio project (Kotlin) — the mobile client
├── server/    Node/Express backend — transcription + AI extraction
├── PROJECT.md
├── MILESTONES.md
└── README.md
\`\`\`

## Getting started

### Backend (`/server`)

\`\`\`bash
cd server
cp .env.example .env   # add your OpenAI + Anthropic API keys
npm install
npm run dev
\`\`\`

### Android app (`/app`)

Open the `app/` folder as a project in Android Studio (Kotlin, min SDK 26+).
See `app/README.md` for the current state and next steps.

## Status

🚧 Early build — see `MILESTONES.md` for where things stand.

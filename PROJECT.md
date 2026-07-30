# BirdScribe — Project Spec

## Pitch

BirdScribe is a hands-free field companion for birders. A wake phrase
triggers recording, the user narrates freely during a birding outing
describing what they see and hear, and the app transcribes the audio and
uses an AI agent to extract a structured checklist of species sighted —
including notes like count, location, and time — from the natural-language
narration. The output is a clean, exportable birding checklist (compatible
with eBird's format) generated from raw spoken observations, with no manual
data entry and no hands-on-phone interaction required in the field.

## Ultimate goal vs. current build approach

The end goal is **always-listening, wake-word-triggered recording** — true
hands-free operation. To get there reliably, the app is being built
incrementally:

1. Build and prove out the core pipeline (recording → transcription → AI
   extraction → checklist) using a simple tap-to-record trigger
2. Once that pipeline is solid, replace the trigger with wake-word detection
   (Picovoice Porcupine) for full hands-free operation

Tap-to-record is a development stepping stone, not the intended end state.

## v1 scope (current build phase)

- Native Android app (Kotlin, Android Studio)
- Tap-to-record trigger (interim — see above)
- Backend transcribes audio (Whisper) and extracts a structured checklist
  (Claude API)
- Checklist is viewable, editable, and saved locally on-device (Room)
- CSV / eBird-format export

## Planned (not yet built)

- Wake-word trigger for true hands-free operation (Picovoice Porcupine) —
  this is the core product goal, being added once the pipeline works
- Offline transcription/extraction
- Multi-user accounts / cloud sync
- iOS version

## Architecture

- **Android app** — Kotlin, `MediaRecorder`/`AudioRecord` for capture,
  Retrofit/OkHttp for networking, Room for local storage
- **Backend** — Node/Express, holds API keys server-side (never embedded in
  the APK), calls Whisper for transcription and Claude for extraction
- **AI extraction** — Claude API call takes a raw transcript and returns
  structured JSON: species, count, notes, confidence

## Why a backend instead of calling AI APIs directly from the phone

API keys embedded in an APK can be extracted by anyone who decompiles it.
Routing calls through a small backend keeps keys server-side and gives room
to add caching, rate limiting, or auth later.

# Mural — personal fork notes (Jakkuno)

This fork exists for **personal use**: to run Mural's Android app with
**Google AI Studio** as the AI provider (Gemini Live voice), as proposed in
upstream PR [Chuloo/mural#129](https://github.com/Chuloo/mural/pull/129)
("Add Google AI Studio provider and Chinese/Thai language support").

## Base

- Upstream base: `Chuloo/mural` `main` @ `c5f9e97` (2026-09-29, "Enable regional Google Play minute purchases (#137)").
- Merge branch: `gemini` — `main` + upstream PR #129 (`feat/google-ai-studio-locales`, tip `f6c15aa`,
  which already integrated upstream `main` @ Sept 26).
- The merge combined upstream PR #129 with upstream `main` commits made after Sept 26
  (managed accounts, Play minute purchases, Talk sheet refinements). Conflicts were resolved
  by keeping both sides' behavior:
  - Android `SettingsScreen.kt`: main's newer page structure (Advanced/Key/About pages) plus
    PR #129's AI provider selector, per-provider key management, usage/billing links and
    data-control links (`vm.aiProvider.*`).
  - Android `MuralViewModel.kt`: `credentials.save/delete(aiProvider)` from PR #129 plus
    main's `providerIssue`/`useAfterSave` logic.
  - Android strings (en/es): union of main's newer error strings and PR #129's provider-aware strings.
  - iOS files: both sides combined (not built here; Android is the target).

## What the app gains

- Settings → Advanced: **AI provider** selector: `OpenAI` or `Google AI Studio`.
- Key management is per-provider (encrypted on device, entered in-app — never in the build).
- With Google AI Studio: voice = `gemini-3.8-live`, teacher/helper = `gemini-3.5-flash-lite`.
- Also includes PR #129's Chinese/Thai language support and related fixes/captions work.

## Build (Android)

Requirements: Java 17, Android SDK Platform 36, Build Tools 35.0.0.

```sh
cd apps/android
./gradlew :app:assembleDebug        # personal install APK
# output: app/build/outputs/apk/debug/app-debug.apk
```

Personal signing note: `apps/android/.signing/debug.keystore` (git-ignored) keeps a stable
signing identity across machines/SDK reinstalls — keep a copy so future APK updates install
over this one. This APK is a personal build: not signed for Google Play, and it cannot update
an installation signed by upstream's own keys (uninstall upstream first if present).

## Fixes after the first release (`v0.1.0-gemini.1` -> `v0.1.0-gemini.2`)

- **Android voice connect** (`GeminiLiveTransport.kt`): the OkHttp URL builder used the `wss`
  scheme, which OkHttp rejects (`IllegalArgumentException: unexpected scheme: wss`) -- OkHttp
  performs the WebSocket upgrade itself and accepts only `http`/`https` URLs. Fixed by building
  the URL with `https`: the transport now completes setup, streams audio, and shows live
  transcription in the app.
- **Google text helper** (`AIProvider.kt`): `gemma-4-31b-it` stalls for interactive helper calls
  (meaning/subtitles stay at "We couldn't get the meaning"). The helper now uses
  `gemini-3.5-flash-lite`, which answers in under a second.
- Both fixes verified end-to-end on a lab Android device (connect, live audio, meaning).

## Independent voice/reasoning providers + Nous Portal (`v0.1.0-gemini.3`)

- Settings → Advanced now has **two independent selectors**:
  - **Voice** — the live-conversation provider: *OpenAI* or *Google AI Studio* (Gemini Live).
  - **Reasoning** — meanings, subtitles, translations, typed replies and word assessments:
    *OpenAI*, *Google AI Studio* or *Nous Portal*.
  Example: Gemini Live for the voice while Nous Portal/Luna handles the text, in the same session.
- **Nous Portal** text provider: OpenAI-compatible `chat/completions` on
  `inference-api.nousresearch.com` (model `openai/gpt-6-luna`; SSE streaming for meanings,
  structured JSON for assessments). Nous is text-only here: no realtime voice API was verified,
  so the Voice selector keeps offering only OpenAI/Google.
- **API keys**: per provider (OpenAI / Google AI Studio / Nous Portal), encrypted on device;
  several can be saved side by side ("_n_ of 3 saved"). Switching the voice or reasoning
  provider asks for the AI-permission review again, then continues.
- Upgrades keep the previous single provider choice, which seeds both selectors.
- Verified end-to-end on a lab device with the **final release APK**: Gemini Live voice session
  (connect, audio, live transcription), Nous-generated typed reply + meanings, word assessments
  (strict JSON), and the on-device usage counter ("Recorded voice time").

## License

MIT, same as upstream (`LICENSE`).

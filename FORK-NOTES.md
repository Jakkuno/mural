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
- With Google AI Studio: voice = `gemini-3.8-live`, teacher/helper = `gemma-4-31b-it`.
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

## License

MIT, same as upstream (`LICENSE`).

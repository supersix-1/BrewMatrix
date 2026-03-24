---
name: brewmatrix-android
description: Build the BrewMatrix specialty coffee Android app using Kotlin and Jetpack Compose. Use this skill for ANY work on the BrewMatrix project — writing Kotlin code, creating Compose UI, setting up Room database, configuring Gradle, implementing the brew timer, ratio calculator, grind memory matrix, or brew log. Also use when fixing build errors, adding animations, theming, or working on any BrewMatrix feature. If the user mentions BrewMatrix, coffee app, brew timer, grind settings, ratio calculator, or anything related to this project, use this skill. Read references/design-system.md before creating ANY UI composable.
---

# BrewMatrix Android Development Skill

BrewMatrix is a precision brew ratio calculator, multi-phase timer, and grind memory journal for specialty coffee. Offline-first, no account required, Android only (Kotlin + Jetpack Compose).

## Workflow Rules — How to Work With the Human

The human building this app may have little or no coding experience. These rules govern how every session should go. They come from real-world lessons about what makes AI-assisted Android development succeed or fail.

**1. Plan before coding.** Before implementing any non-trivial feature, enter Plan Mode (think through the approach, list which files will be created/modified, identify dependencies) and explain the plan to the human in plain English. Wait for approval before writing code. Never make architectural decisions silently.

**2. One vertical slice at a time.** Build the simplest working version of a feature end-to-end (entity → DAO → repository → ViewModel → Compose screen) before adding complexity. Never try to build an entire feature in one giant prompt. Break it into steps the human can verify along the way.

**3. Verify after every change.** After writing or modifying code, run `./gradlew assembleDebug` to confirm the build compiles with zero errors. If the build fails, fix it immediately before doing anything else. Tell the human the build status in plain language ("That compiled successfully" or "There's a build error — here's what went wrong and how I'll fix it").

**4. Commit after every successful change.** Treat Git commits as save points. After each phase or sub-feature compiles and works, commit with a descriptive message. This lets the human roll back if something goes wrong later. Use the format: `git add -A && git commit -m "Phase X: description of what was added"`.

**5. Clear context between unrelated tasks.** When switching from one feature area to another (e.g., finishing the calculator and starting the timer), recommend the human run `/clear` in Claude Code to reset context. Long, unfocused context leads to worse output.

**6. The three-strike rule for stuck problems.** If a fix fails twice, do NOT keep patching. Instead: stop, explain to the human what's going wrong, run `/clear`, and re-approach the problem with a fresh, better-structured prompt that incorporates what was learned from the failures.

**7. Ask before assuming.** If a requirement is ambiguous, ask the human a specific question rather than guessing. One targeted question is better than building the wrong thing and having to undo it.

**8. Explain what you built.** After completing a chunk of work, give a brief plain-English summary: what files were created/changed, what the code does, and what the human should test on their emulator or device. The human can't read Kotlin fluently — they need you to translate.

## Tech Stack (Mandatory — No Substitutions)

- **Language:** Kotlin only. No Java files.
- **UI:** Jetpack Compose. No XML layouts.
- **Min SDK:** 26. **Target SDK:** 35.
- **Architecture:** MVVM with Repository pattern.
- **Database:** Room with **KSP** (NOT kapt).
- **DI:** Manual AppContainer (no Hilt/Dagger).
- **Async:** Kotlin Coroutines + Flow. No LiveData.
- **Navigation:** Compose Navigation (type-safe routes).
- **Fonts:** DM Sans + DM Mono bundled in `res/font/`. No runtime font downloads.
- **Icons:** Material Symbols Rounded (filled, weight 400, 24dp).

## Version Catalog — All Versions Live in One Place

All dependency versions go in `gradle/libs.versions.toml`. Never hardcode version numbers in `build.gradle.kts` files. This prevents version drift, which is the #1 cause of mysterious build failures in Android projects.

When setting up the project, create `libs.versions.toml` with pinned, compatible versions for:
- Kotlin, KSP (must match Kotlin version), AGP
- Compose BOM (pins all Compose library versions together)
- Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`)
- Room, Navigation, Coroutines, Lifecycle

When the human tells you which version of Android Studio or Kotlin they're using, pin all other versions to match. If unsure about compatibility, ask — do not guess.

## Critical Android Pitfalls

These cause real build failures. Memorize them:

1. **Room requires KSP, not kapt.** Use `com.google.devtools.ksp` plugin. KSP version must match Kotlin version (e.g., KSP `2.0.0-1.0.21` for Kotlin `2.0.0`). Read the version from `libs.versions.toml`.

2. **Compose compiler is a Kotlin plugin since Kotlin 2.0.** Apply `org.jetbrains.kotlin.plugin.compose` in the plugins block. Do NOT set `kotlinCompilerExtensionVersion` — that's deprecated and causes conflicts.

3. **Do not invent APIs.** If unsure whether a function, Modifier, or API exists, say "I'm not 100% certain this API exists — please verify." Never silently fabricate function signatures.

4. **Modifier ordering matters.** `Modifier.padding(16.dp).background(color)` pads then colors (padding is transparent). `Modifier.background(color).padding(16.dp)` colors then pads (padding area is colored). Think about intent before writing chains.

5. **Use `collectAsStateWithLifecycle()`** (from `androidx.lifecycle.compose`) to collect Flows in composables. Not `collectAsState()`. This avoids wasted recomposition when the app is backgrounded.

6. **LazyColumn for any list.** Never `Column` with `forEach` for dynamic data. Column renders every item at once — it will lag on 20+ items and crash on hundreds.

7. **Wake lock lifecycle.** The brew timer needs `android.permission.WAKE_LOCK` in the manifest. Acquire when the timer starts, release when it stops OR when the composable leaves composition. Leaked wake locks drain the battery and will get the app 1-star reviews.

8. **Niche libraries may not work well.** If Claude Code struggles to produce working code for a library after two attempts, suggest a more popular alternative and explain why to the human.

## Proactive Quality Rules

Audits of real apps built with AI coding tools found the same problems in nearly every project: missing input validation, no error handling, hardcoded secrets, and tests that assert nothing. Prevent these proactively:

**Input validation — every user input must be validated:**
- Ratio values: minimum 1.0, maximum 99.999. Reject non-numeric input gracefully.
- Coffee/water grams: minimum 0.1, maximum 9999. No negative numbers.
- Grind setting text: max 50 characters. Trim whitespace.
- Bean/grinder names: max 100 characters. Cannot be blank.
- Timer phase durations: minimum 1 second, maximum 3600 seconds.
- Show clear, friendly error messages (e.g., "Ratio must be between 1 and 99.999") — never crash or silently ignore bad input.

**Error states — every screen must handle failure gracefully:**
- Database read failures: show a "Something went wrong" message with a retry option, not a crash.
- Empty states: every list screen has a designed empty state (see design-system.md), never a blank white screen.
- Loading states: if any operation takes time, show a progress indicator.

**No hardcoded secrets:** Google Play Billing keys, API keys, or any credentials must go in `local.properties` (gitignored) or `BuildConfig` fields — never in source code files.

**Tests must assert real behavior:** If writing a test, it must verify actual output against expected values. A test that always passes regardless of code correctness is worse than no test — it gives false confidence. When writing a test for the ratio calculator, assert that 15g coffee at 1:16.667 produces 250.005g water, not just that the function "doesn't crash."

## Project Architecture

```
com.brewmatrix.app/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   └── entity/
│   ├── repository/
│   └── model/
├── di/
│   └── AppContainer.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt        (full color system)
│   │   ├── Type.kt          (DM Sans + DM Mono)
│   │   ├── Theme.kt         (Light/Dark/Premium themes)
│   │   └── Shape.kt         (corner radii system)
│   ├── components/           (shared composables)
│   ├── calculator/
│   ├── timer/
│   ├── grindmemory/
│   ├── brewlog/
│   └── settings/
├── navigation/
│   └── NavGraph.kt
└── BrewMatrixApp.kt         (custom Application with AppContainer)
```

Layer rules:
- **Composables:** Display state, forward events. No business logic, no database calls.
- **ViewModels:** Hold state as `StateFlow<XxxUiState>`. Call repositories. Never import Room or DAO classes.
- **Repositories:** Single source of truth. Talk to DAOs. Provided by AppContainer.
- **Entities/DAOs:** Pure data access. No business logic.

## Database Schema

Seven Room entities. All use auto-generated Long primary keys.

**RatioPreset:** id, name (String), ratio (Double), isDefault (Boolean), sortOrder (Int)
- Ship defaults: V60 (16.667), AeroPress (12.0), French Press (15.0), Chemex (15.5), Espresso (2.0), Cold Brew (5.0)

**TimerPreset:** id, name (String), ratioPresetId (nullable Long FK), totalDurationSeconds (Int)

**TimerPhase:** id, timerPresetId (Long FK), phaseName (String), durationSeconds (Int), targetWaterGrams (nullable Double), sortOrder (Int)

**Grinder:** id, name (String), type (String), createdAt (Long), lastUsedAt (Long)

**Bean:** id, name (String), roaster (String), origin (nullable String), createdAt (Long), lastUsedAt (Long)

**GrindSetting:** id, grinderId (Long FK), beanId (Long FK), grindValue (String), notes (nullable String), linkedRatioPresetId (nullable Long FK), linkedTimerPresetId (nullable Long FK), lastUsedAt (Long), createdAt (Long)
- Unique constraint on (grinderId + beanId)

**BrewLog:** id, beanId (nullable Long FK), grinderId (nullable Long FK), grindSettingId (nullable Long FK), ratioUsed (Double), totalBrewTimeSeconds (Int), rating (nullable Int 1–5), note (nullable String), brewedAt (Long)

## Build Phases — Sequential, One at a Time

Each phase is a vertical slice. Complete it, verify it compiles (`./gradlew assembleDebug`), confirm with the human, and commit to Git before starting the next one.

### Phase 1: Project Skeleton
**Build:** Android project with Compose, Room (KSP), Navigation, manual AppContainer DI. `libs.versions.toml` with all pinned versions. Theme files (Color.kt, Type.kt, Theme.kt, Shape.kt) with the complete color system. Bottom nav bar with four placeholder screens.
**Verify:** `./gradlew assembleDebug` passes. App launches on emulator and shows bottom nav with four tabs.
**Commit:** `git add -A && git commit -m "Phase 1: Project skeleton with theme, navigation, and placeholder screens"`

### Phase 2: Brew Ratio Calculator
**Build:** Calculator screen with two input fields (coffee g, water g) that update each other in real-time. Ratio selector with default presets from database. Save/edit custom presets. Animated number transitions. Input validation (numbers only, within range).
**Verify:** `./gradlew assembleDebug` passes. Human tests: change coffee grams and water updates; change ratio and values recalculate; save a preset; type letters or negative numbers and see friendly errors.
**Commit:** `git add -A && git commit -m "Phase 2: Brew ratio calculator with presets and validation"`

### Phase 3: Multi-Phase Brew Timer
**Build:** Timer screen with phase list, large countdown (72sp+ monospace), start/pause/reset. Phase auto-advance with haptic. Screen wake lock. Completion animation. Timer presets (save/load). Phase duration validation.
**Verify:** `./gradlew assembleDebug` passes. Human tests: start timer, screen stays on, phases auto-advance with vibration, timer completes with animation, pause/resume works.
**Commit:** `git add -A && git commit -m "Phase 3: Multi-phase brew timer with wake lock and presets"`

### Phase 4: Grind Memory Matrix
**Build:** Grinder CRUD, Bean CRUD, GrindSetting grid linking grinder + bean. Quick-link to ratio/timer presets. Most-recently-used sorting. Swipe-to-delete with confirmation dialog. Name validation (not blank, not too long).
**Verify:** `./gradlew assembleDebug` passes. Human tests: add grinder, add bean, save grind setting, see it appear at top of list, swipe to delete, confirm dialog works.
**Commit:** `git add -A && git commit -m "Phase 4: Grind memory matrix with CRUD and linking"`

### Phase 5: Brew Log
**Build:** Post-brew rating bottom sheet (1–5 scale + optional note). Reverse-chronological log list. Auto-populate from timer session if applicable. Empty state with illustration.
**Verify:** `./gradlew assembleDebug` passes. Human tests: complete a brew timer, rating sheet appears, save a rating, see it in the log list.
**Commit:** `git add -A && git commit -m "Phase 5: Brew log with post-brew rating"`

### Phase 6: Settings & Tip Jar
**Build:** Settings screen (default ratio, default timer, theme selector light/dark/system). Tip Jar with Google Play Billing (three tiers). Premium theme unlocks. No hardcoded billing keys.
**Verify:** `./gradlew assembleDebug` passes. Theme switching works. Tip Jar UI renders correctly (billing can only be fully tested on a real device with a Play Console test account).
**Commit:** `git add -A && git commit -m "Phase 6: Settings and tip jar"`

### Phase 7: Polish
**Build:** Empty state illustrations for every screen. Staggered list load animations. Screen transition animations. Edge case audit (what happens with 0 items, 500 items, very long names, rapid tapping). Final visual QA against design-system.md.
**Verify:** `./gradlew assembleDebug` passes. Human visually inspects every screen against the design spec. Check: warm colors (not white/black), gradient buttons (not flat), animated transitions (not instant), correct fonts.
**Commit:** `git add -A && git commit -m "Phase 7: Visual polish, animations, and empty states"`

## Compose Guidelines

**State management:**
- Screen state lives in ViewModel as immutable data class ending in `UiState`
- Composables receive state + emit events via callback lambdas
- Use `collectAsStateWithLifecycle()` to observe state

**Stateless composables:**
- Prefer composables that take parameters and callbacks
- State hoisted to screen-level composable
- Only screen-level composable creates ViewModel (via factory from AppContainer)

**Performance:**
- `remember` and `derivedStateOf` for computed values
- `key()` in LazyColumn items for stable identity
- No object allocations inside composable function bodies

**Theming:**
- Always reference `MaterialTheme.colorScheme` — never hardcode colors
- Extended colors (gradients, accents) via `CompositionLocal`

## Debugging — When the Human Pastes an Error

Claude Code cannot see the emulator, Logcat, or the running app. The human must paste error text. When they do:

1. Read the FULL stack trace — the root cause is usually buried, not on the first line.
2. **Gradle/build errors:** Almost always a version mismatch. Check Kotlin ↔ KSP ↔ Compose BOM ↔ AGP in `libs.versions.toml` first.
3. **Room errors:** Check entity annotations, DAO return types, and whether the database version was incremented after schema changes.
4. **Compose crashes:** Look for missing `remember`, state collection outside composition, or modifier ordering bugs.
5. **Never delete a failing test** to make the build pass. Fix the code the test is catching.
6. **If the error is unclear,** ask the human to also paste the few lines of code surrounding where the error points. More context is always better than guessing.

## Companion Skills

Two open-source Claude Code skills provide additional Android safety nets. The human can install them alongside this skill:
- **dpconde/claude-android-skill** — Clean Architecture + MVVM patterns based on Google's NowInAndroid reference app.
- **aldefy/compose-skill** — Compose-specific guidance backed by actual AndroidX source code. Catches incorrect `remember` usage, unstable recompositions, and deprecated navigation patterns.

These are optional but recommended, especially for Compose UI work.

## What NOT To Build (V1 Exclusions)

These are explicitly out of scope. Do not build them, do not add placeholders, do not mention them in the UI:
- Cloud sync, user accounts, social features, sharing
- Bluetooth scale integration
- Water temperature tracking
- Charts or analytics for the brew log
- iOS or cross-platform anything
- Onboarding tutorial or splash screen
- Widget, notification, or watch companion

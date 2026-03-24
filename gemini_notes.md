# BrewMatrix Project Notes for AI Agent

## Project Overview
- **Name:** BrewMatrix
- **Tech Stack:** Android, Kotlin, Jetpack Compose, Room Database.
- **Vibe:** Currently in "vibecoding" mode—rapid development with destructive migrations enabled.

## Database Schema (`AppDatabase.kt`)
- **Entities:**
    - `RatioPreset`: Stores coffee-to-water ratios (e.g., V60 Classic, Chemex).
    - `TimerPreset`: Parent entity for brew workflows.
    - `TimerPhase`: Child entity for `TimerPreset` (e.g., Bloom, Pour, Drawdown).
- **Relationships:** `TimerPhase` has a foreign key to `TimerPreset` with `ON DELETE CASCADE`.
- **Seeding:** Defaults are prepopulated in `AppDatabase.Callback` using an IO coroutine.

## Current Technical Debt / Risks
1. **Seeding Race Condition:** `seedDefaults` runs in a `launch`. The UI might query the `Flow` before data is inserted, appearing empty on first launch.
2. **Transaction Integrity:** Seeding logic for `TimerPreset` and `TimerPhase` is not currently wrapped in a formal database transaction, risking partial data if interrupted.
3. **Localization:** Preset names ("Bloom", "V60", etc.) are hardcoded as Strings in the DB seeding logic.
4. **Data Loss Risk:** `.fallbackToDestructiveMigration()` is active. Schema changes will wipe user data.

## Recent Context
- Reviewed DAOs: `RatioPresetDao`, `TimerPresetDao`, `TimerPhaseDao`.
- Reviewing `build.gradle.kts` for plugin and dependency alignment.

## Future Recommendations
- Implement a `Transaction` wrapper for multi-step seeding.
- Consider a "System-provided" flag vs "User-created" for presets to allow updating defaults without overwriting user customizations.
- Move away from destructive migrations before any public/alpha release.

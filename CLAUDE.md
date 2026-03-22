# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Key rules
- Use the brewmatrix-android skill for all work on this project
- All dependency versions go in gradle/libs.versions.toml
- Room uses KSP, not kapt
- Compose compiler is a Kotlin plugin (org.jetbrains.kotlin.plugin.compose)
- Run ./gradlew assembleDebug after every change to verify the build
- Commit to Git after every successful phase

## Project Overview

BrewMatrix is an offline-only Android app (Kotlin + Jetpack Compose) for specialty coffee brewing. It provides a brew ratio calculator, multi-phase brew timer, grind memory journal, and brew log. The authoritative spec is `BrewMatrix_Project_Bible.md` in the project root — read it before making architectural decisions.

Package name: `com.brewmatrix.app`

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test                         # All unit tests
./gradlew testDebugUnitTest            # Debug unit tests only
./gradlew test --tests "com.brewmatrix.app.SomeTest"  # Single test class

# Lint
./gradlew lint
./gradlew lintDebug

# Format check (if ktlint is configured)
./gradlew ktlintCheck
./gradlew ktlintFormat

# Clean
./gradlew clean
```

## Architecture

- **MVVM with strict Unidirectional Data Flow (UDF)** — zero business logic in UI layer
- **Single-Activity** architecture with Jetpack Compose Navigation (no Fragments, no XML)
- **Room database** is the single source of truth for all persistent data
- **Repository pattern** — ViewModels never touch DAOs directly; all DB ops via coroutines
- **StateFlow only** for observable state in ViewModels — no LiveData
- **Manual dependency injection** via AppContainer — no Hilt/Dagger

## Code Style

- Kotlin only — no Java
- One file per screen composable, one file per ViewModel
- Entity, DAO, and Database classes each get their own file
- Named arguments for any function call with more than 2 parameters
- Data classes for all state objects

## Prohibited

- No XML layouts, no Fragments, no Java files
- No LiveData
- No Dagger/Hilt
- No Retrofit or networking libraries — app is offline-only
- No Firebase — no analytics, no crashlytics, no cloud services
- Do not add any dependency not explicitly listed in the build phase instructions (Project Bible Section 13)

## Visual Design (MANDATORY — See Project Bible Section 3)

- **Fonts:** Bundle DM Sans (labels/body) + DM Mono (numbers) as custom fonts in `res/font/`. Never use default Roboto for display text or numbers
- **Backgrounds:** Light theme = warm off-white `#FAF7F2`, dark theme = warm charcoal `#121010`. Never pure white or pure black
- **Color palette:** Custom warm brown/amber/copper — never default Material 3 purple/teal seed colors
- **Buttons:** Primary action buttons use `Brush.horizontalGradient` with warm amber-to-brown gradient — no flat rectangles
- **Numeric displays:** All timer/calculator numbers use monospace font (DM Mono) to prevent layout jitter
- **Cards:** `RoundedCornerShape(16.dp)` with soft warm shadows — not default Material elevation
- **Animations required on all state changes:**
  - `AnimatedVisibility`, `AnimatedContent`, `animateFloatAsState` etc. — nothing appears/disappears instantly
  - List items: staggered fade-up animation on load (50ms delay per item)
  - Calculator numbers: `AnimatedContent` with vertical slide transition
- **Touch targets:** Minimum 48.dp for tappable elements, 56.dp for buttons
- **Spacing:** Minimum 16.dp horizontal content padding, 24.dp vertical between sections
- **Icons:** Material Symbols Rounded (filled), never outlined
- **Chips:** Fully pill-shaped `RoundedCornerShape(24.dp)`
- **FAB:** Colored shadow glow with `spotColor = AccentColor.copy(alpha = 0.25f)`

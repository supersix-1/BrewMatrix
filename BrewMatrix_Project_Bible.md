# BrewMatrix — Project Bible

> **Purpose:** This document is the single source of truth for building BrewMatrix entirely through vibecoding with Claude Code. It follows the Two-Layer AI Protocol: this file IS the Design AI output. Feed sections of it sequentially to the Implementation AI (Claude Code in your terminal). Do not deviate from the architecture described here.

---

## Table of Contents

1. [Product Overview](#1-product-overview)
2. [Target User & Market Position](#2-target-user--market-position)
3. [Design Language & Visual Identity](#3-design-language--visual-identity)
4. [Core Feature Set](#4-core-feature-set)
5. [Feature Exclusion List (YAGNI)](#5-feature-exclusion-list-yagni)
6. [CLAUDE.md — Implementation AI Rules](#6-claudemd--implementation-ai-rules)
7. [Database Schema](#7-database-schema)
8. [App Architecture & Data Flow](#8-app-architecture--data-flow)
9. [Screen-by-Screen UI Specification](#9-screen-by-screen-ui-specification)
10. [Brew Timer Logic](#10-brew-timer-logic)
11. [Grind Memory Matrix Logic](#11-grind-memory-matrix-logic)
12. [Monetization Implementation](#12-monetization-implementation)
13. [Sequential Build Phases](#13-sequential-build-phases)
14. [Testing Checklist](#14-testing-checklist)
15. [App Store Optimization (ASO)](#15-app-store-optimization-aso)
16. [Post-Launch Roadmap](#16-post-launch-roadmap)

---

## 1. Product Overview

**App Name:** BrewMatrix

**One-Liner:** A precision brew ratio calculator, multi-phase timer, and grind memory journal for specialty coffee — offline, private, no account required.

**Elevator Pitch:** BrewMatrix solves three daily pain points for specialty coffee brewers: (1) calculating exact water/coffee amounts using non-integer ratios like 1:16.667, (2) timing multi-phase pour-over sequences without the screen dimming, and (3) remembering which grind setting worked for which bean on which grinder. It does all three in a single, offline-first app with zero account creation and zero ads.

**Platform:** Android (Kotlin + Jetpack Compose). No iOS version planned for V1.

**Revenue Model:** Hybrid freemium — 100% of core functionality is free forever. Revenue comes from a voluntary "Tip Jar" via native Google Play In-App Purchases, plus one-time cosmetic upgrades (premium themes, custom app icons).

---

## 2. Target User & Market Position

### Who Is This For

The primary user is someone who brews specialty coffee at home using manual methods (pour-over, French press, AeroPress, Chemex, V60, Kalita Wave, etc.). They own at least one manual grinder (Comandante, 1Zpresso, Timemore, Baratza). They care about precision. They currently track grind settings on sticky notes, in Apple Notes, or in their head — and they lose that information constantly.

### Why Now — The Sleeping Competition

Existing specialty coffee apps on the Play Store exhibit textbook "sleeping competition" symptoms:

- Average ratings of 2.5–3.5 stars across the top 5 competitors
- Forced account creation before the app does anything useful
- Ratio calculators that only accept whole-number ratios (1:15, 1:16, 1:17 — no decimals)
- Timers that don't prevent screen dimoff — useless when your hands are full of hot water
- No concept of saving grind settings per bean per grinder
- Aggressive interstitial ads or $5+ subscriptions for basic calculator features

### The Differentiators

1. **Non-integer ratio precision** — supports ratios like 1:16.667 out of the box
2. **Grind Memory Matrix** — recalls your last grind setting for any bean + grinder combination
3. **Screen wake lock** — screen stays on during active brew timer (uses Android wake lock API)
4. **Zero friction onboarding** — no account, no sign-up, no cloud sync, no splash screen tutorial. Open the app, start brewing.
5. **Offline-first** — every feature works without internet, forever
6. **No ads, ever** — the free version has zero advertising

---

## 3. Design Language & Visual Identity

> **THIS SECTION IS MANDATORY.** Every screen, component, and interaction in BrewMatrix must conform to the design system defined here. The app must look and feel like a premium, high-end product — something that belongs next to apps made by well-funded design studios, not a hobbyist side project. This is the #1 competitive weapon against legacy competitors with dated UIs. If the AI generates anything that looks generic, flat, or stock Material Design, reject it and regenerate.

### 3.1 Design Philosophy: "Precision Craft"

The visual identity of BrewMatrix should evoke the same feeling as the specialty coffee experience itself: warm, intentional, precise, and elevated. Think high-end coffee packaging design meets a Swiss-designed instrument. Every pixel should feel deliberate.

**Core design principles:**

- **Warm Luxury** — This is not a cold, clinical utility app. It should feel like holding a beautifully designed coffee tool — warm tones, rich surfaces, tactile depth. Think Fellow Stagg kettle packaging, not a spreadsheet.
- **Confident Restraint** — The UI is minimal in *quantity* but rich in *quality*. Few elements on screen, but every element is visually refined. Generous whitespace. Nothing feels crowded or cheap.
- **Depth & Dimension** — Surfaces have subtle layering. Cards float above backgrounds. Active states glow. The UI has a sense of physical depth created through shadows, gradients, and layered translucency — not flat colored rectangles.
- **Motion With Purpose** — Every transition and state change has a considered animation. Elements don't just appear — they ease in. Numbers don't just change — they animate. Nothing is instant or jarring.

### 3.2 Color System

**Do NOT use the default Material 3 color seed and call it a day.** Build a custom color system.

**Light Theme — "Morning Brew"**

| Role | Color | Hex | Notes |
|---|---|---|---|
| Background | Warm off-white | `#FAF7F2` | Slightly warm cream, NOT pure white |
| Surface / Cards | Soft warm white | `#FFFFFF` | Cards with subtle warm shadow |
| Primary | Deep espresso brown | `#3C2415` | Rich, dark, confident |
| Primary Gradient Start | Warm amber | `#C17D3A` | Used for gradient accents |
| Primary Gradient End | Deep roast | `#6B3A1F` | Gradient flows warm → dark |
| Secondary | Muted sage green | `#7A9E7E` | Subtle organic accent |
| Accent / Interactive | Copper gold | `#B8860B` | For active states, FABs, highlights |
| On-Background Text | Near black | `#1A1A1A` | NOT pure #000000 |
| Secondary Text | Warm gray | `#6B6560` | For labels, timestamps |
| Subtle Borders | Warm light gray | `#E8E2DA` | For dividers, card outlines |
| Error / Warning | Warm red | `#C0392B` | Not aggressive, still warm-toned |

**Dark Theme — "Late Night Pour"**

| Role | Color | Hex | Notes |
|---|---|---|---|
| Background | Deep charcoal | `#121010` | Warm-tinted black, NOT pure #000 |
| Surface / Cards | Dark warm gray | `#1E1B18` | Slight warmth to avoid cold/sterile |
| Primary | Warm gold | `#D4A05A` | Prominent and warm against dark |
| Primary Gradient Start | Bright amber | `#E8B86D` | Glowing, warm |
| Primary Gradient End | Burnished copper | `#A0693D` | Gradient flows light → rich |
| Secondary | Soft olive | `#8FAF8A` | Muted green |
| Accent / Interactive | Bright copper | `#D4944A` | Punchy against dark surfaces |
| On-Surface Text | Warm white | `#F0EBE3` | NOT pure white, slightly warm |
| Secondary Text | Muted tan | `#9C9489` | For labels, timestamps |
| Subtle Borders | Deep warm gray | `#2E2926` | Barely visible structure |
| Error / Warning | Warm salmon | `#E74C3C` | Visible without being harsh |

**Premium Theme Colors (Paid Unlocks):**

- **AMOLED Black** — True `#000000` background, `#D4A05A` gold accents, `#F0EBE3` text. Maximum contrast, maximum battery savings on OLED screens.
- **Ocean Blue** — `#0A1628` deep navy background, `#4A90D9` steel blue primary, `#6DBAE0` light blue gradient accent. Cool-toned, calm, like a predawn brew session.
- **Warm Latte** — `#F5EDE0` creamy off-white background, `#8B6F4E` latte brown primary, `#D4B896` light caramel accents. Ultra-warm, cozy, like a coffee shop in autumn.

### 3.3 Typography

**Do NOT use default Roboto for display text.** Use a custom font pairing that feels distinctive and premium.

| Role | Font | Weight | Size | Notes |
|---|---|---|---|---|
| Timer Countdown | **DM Mono** or **JetBrains Mono** | Medium | 72–96sp | Monospaced for stable digit width. Digits must never shift layout as they count down. |
| Large Numbers (Calculator) | **DM Mono** or **JetBrains Mono** | Regular | 48–56sp | Same monospace family for all numeric displays |
| Section Headers | **DM Sans** | Bold | 20–24sp | Clean geometric sans-serif with personality |
| Body Text / Labels | **DM Sans** | Regular | 14–16sp | Pairs with DM Mono for visual cohesion |
| Chip / Tag Text | **DM Sans** | Medium | 12–14sp | Compact, readable |
| Caption / Timestamps | **DM Sans** | Regular | 12sp | Muted color, secondary importance |

**Typography rules:**

- Include DM Sans and DM Mono as bundled font assets in `res/font/` — do NOT rely on Google Fonts download at runtime (this is an offline-first app)
- All numeric displays (timer, calculator, grind settings) use the monospace font — this prevents layout jitter when digits change
- Letter spacing on the timer countdown: `+0.05em` for breathing room
- All caps for phase labels during active timer (e.g., "BLOOM", "FIRST POUR") with `letterSpacing = 2.sp`

### 3.4 Gradients & Surface Treatment

**Gradients are a first-class citizen in this app's design language.** They are not decorative afterthoughts — they are structural visual elements that communicate hierarchy and state.

**Where to use gradients:**

- **"Start Brew" button:** Full-width with a horizontal warm gradient (`Primary Gradient Start → Primary Gradient End`). This is the most important CTA in the app and must feel rich, tactile, and inviting — not a flat colored rectangle. Apply a subtle inner shadow or border highlight to give it physical depth. Rounded corners at `16.dp`.
- **Active timer phase progress bar:** Animated gradient that shifts warmth as the phase progresses. Starts cooler amber, ends richer copper as the countdown nears zero.
- **Bottom navigation bar background:** Subtle vertical gradient from `Surface` to a slightly darker shade, creating a sense of grounded weight. The nav bar should feel like a solid shelf the content sits on.
- **Card backgrounds on Grind Memory:** Very subtle radial gradient from center-light to edge-slightly-darker, creating a sense of depth. This replaces flat filled cards.
- **Tip Jar buttons:** Each tip tier has a distinct gradient — espresso is a short dark gradient, pour-over is a medium warm gradient, bag of beans is a full rich gold-to-brown gradient. The visual richness of the button scales with the tip amount.
- **Timer completion celebration:** When the timer finishes, a brief radial gradient pulse animation emanates from the center (warm gold outward to transparent). Subtle, rewarding, not childish.

**Where NOT to use gradients:**

- Do not apply gradients to body text or small UI elements — it looks cheap at small scale
- Do not use gradients on the bottom nav icons themselves — keep icons solid
- Never use rainbow or multi-hue gradients — all gradients stay within the warm amber/brown/copper family (or theme-appropriate family for premium themes)

**Gradient implementation in Compose:**

```kotlin
// Use Brush.linearGradient and Brush.radialGradient — NOT backgroundColor
Modifier.background(
    brush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    ),
    shape = RoundedCornerShape(16.dp)
)
```

### 3.5 Elevation, Shadows & Depth

- **Cards** use `tonalElevation = 2.dp` PLUS a custom soft shadow: `Modifier.shadow(elevation = 8.dp, spotColor = Color(0x15000000), shape = RoundedCornerShape(16.dp))`. The shadow should feel warm and diffuse, not harsh or dark.
- **FAB** uses a larger shadow (`12.dp`) with the accent color tinted into the shadow: `spotColor = AccentColor.copy(alpha = 0.25f)`. This creates a subtle colored glow beneath the button.
- **Bottom sheets** (brew log rating) use a frosted-glass effect at the top edge: a thin gradient from `Surface.copy(alpha = 0.0f)` to `Surface` over 24.dp, creating a soft bleed into the content behind.
- **Active states** (pressed buttons, selected chips) briefly increase shadow depth and shift shadow color warmer, giving a tactile "press" feel.

### 3.6 Corner Radii (Consistent System)

| Element | Radius | Notes |
|---|---|---|
| Cards | `16.dp` | Generously rounded, modern |
| Buttons (large) | `16.dp` | Matches cards for visual harmony |
| Chips / Tags | `24.dp` | Fully pill-shaped |
| Input Fields | `12.dp` | Slightly less rounded than cards |
| Bottom Sheet | `24.dp` top only | Soft grab handle feel |
| FAB | `20.dp` | Rounded square, not circle |
| Bottom Nav Bar | `0.dp` | Flush to screen edges |

### 3.7 Iconography

- Use **Material Symbols Rounded** (not the default outlined set) — rounded icons feel warmer and more approachable
- Icon weight: 400 (regular), optical size: 24dp
- Bottom nav icons: 24dp, with labels
- Interactive icons in cards: 20dp
- Never use outlined-style icons — they feel thin and clinical. Everything is filled or rounded.

### 3.8 Animation & Motion

**Every screen transition and state change must be animated.** Nothing appears or disappears instantly.

| Interaction | Animation | Duration | Easing |
|---|---|---|---|
| Screen navigation | Shared axis (horizontal slide + fade) | 300ms | `EaseInOutCubic` |
| Card appear (list load) | Staggered fade-up: each card fades in and slides up 16dp, with 50ms stagger delay per item | 250ms each | `EaseOutCubic` |
| Number change (calculator) | Animated counter — digits roll/crossfade to new value, not instant replacement | 150ms | `EaseInOutQuad` |
| Timer countdown tick | Subtle scale pulse on the seconds digit (1.0 → 1.02 → 1.0) every second | 100ms | `EaseOut` |
| Phase transition | Current phase chip scales down + fades, next phase chip scales up + highlights with glow | 400ms | Spring physics |
| Button press | Scale down to 0.96 on press, spring back to 1.0 on release | 100ms down, spring release | `EaseOut` + spring |
| FAB tap | Ripple + brief shadow expansion | Default Material ripple | — |
| Chip selection | Fill color animates from transparent to filled, border fades | 200ms | `EaseInOut` |
| Brew complete | Radial warm glow pulse from center + haptic | 600ms | `EaseOut` |
| Swipe to delete | Card slides right with opacity fade + red background reveal | 250ms | `EaseInCubic` |

**Animation implementation notes:**

- Use `animateFloatAsState`, `AnimatedVisibility`, `AnimatedContent`, and `Animatable` from Compose animation APIs
- For staggered list animations, use `LaunchedEffect` with incremental delay per item index
- For the number roller effect on the calculator, use `AnimatedContent` with `transitionSpec` using `slideInVertically + fadeIn` paired with `slideOutVertically + fadeOut`
- Spring physics: use `spring(dampingRatio = 0.6f, stiffness = 400f)` for bouncy, tactile feel

### 3.9 Empty States

Empty states are not an afterthought. They are the user's FIRST impression of each screen.

- **Every empty state** has a custom illustration (simple, warm-toned SVG or Compose Canvas drawing — a coffee cup, a grinder, coffee beans, steam wisps)
- **Illustration style:** Minimal line art in the Primary color with gradient fills. Not clip art. Not emoji. Think single-weight stroke illustrations you'd see on premium coffee packaging.
- **Below the illustration:** A short, warm, human headline (e.g., "No beans in the memory yet") and a secondary line of helpful text (e.g., "Tap + to save your first grind setting")
- **The CTA button** in the empty state should match the same gradient styling as the "Start Brew" button — prominent, inviting, impossible to miss

### 3.10 Anti-Patterns — Things That MUST NOT Happen

These are explicit visual failures that will make the app look cheap, generic, or dated. If the AI generates any of these, reject and regenerate:

- **Pure white (#FFFFFF) backgrounds** — Always use the warm off-white (`#FAF7F2`) in light mode
- **Pure black (#000000) backgrounds in dark mode** — Use warm charcoal (`#121010`) unless the user has purchased AMOLED theme
- **Default Roboto on display elements** — All headers and numbers must use the custom font pairing
- **Flat colored rectangles as buttons** — Primary buttons must use gradients
- **Gray placeholder illustrations** — Empty states must have warm, branded illustrations
- **Instant state changes with no animation** — Every visibility change, number change, and navigation must animate
- **Thin 1dp divider lines everywhere** — Use spacing and subtle shadow/elevation changes to create separation, not heavy dividers. If a divider is truly needed, use `0.5dp` in the `Subtle Borders` color.
- **Default Material 3 purple/teal seed color** — The app has a custom warm color system. No purple. No teal. No default blue.
- **Harsh drop shadows** — Shadows must be diffuse, warm, and subtle
- **Dense, cramped layouts** — Generous padding everywhere. Minimum `16.dp` horizontal padding on all screen content. `24.dp` vertical spacing between major sections.
- **Small touch targets** — All tappable elements minimum `48.dp` height. Buttons minimum `56.dp` height.
- **Aggressive ALL CAPS everywhere** — Only the active timer phase name uses all caps. Everything else is sentence case or title case.

---

## 4. Core Feature Set

### Feature 1: Brew Ratio Calculator

- User inputs EITHER their coffee dose (grams) OR their target water yield (grams)
- App instantly calculates the counterpart based on the active ratio
- Ratios are fully customizable with decimal precision (minimum 1:1, maximum 1:99.999)
- Users can save named ratio presets (e.g., "James Hoffmann V60" = 1:16.667, "AeroPress Standard" = 1:12)
- Default presets ship with the app for common methods
- Calculator updates in real-time as the user adjusts inputs — no "calculate" button

### Feature 2: Multi-Phase Brew Timer

- Timer supports multiple sequential phases (e.g., Phase 1: Bloom 45s → Phase 2: First Pour 30s → Phase 3: Second Pour 30s → Phase 4: Drawdown 90s)
- Each phase has a name, a duration, and optionally a target water weight for that phase
- Timer auto-advances between phases with a distinct haptic + audio cue at each phase transition
- Screen remains ON for the entire timer duration via Android wake lock
- Timer phases are configurable per brew method and saved as named presets
- Large, high-contrast time display readable at arm's length (minimum 72sp font for the active countdown)

### Feature 3: Grind Memory Matrix

- User creates entries for their grinders (name + type, e.g., "Comandante C40" / "1Zpresso JX-Pro")
- User creates entries for their beans (name + roaster + optional origin, e.g., "Yirgacheffe / Onyx Coffee Lab / Ethiopia")
- For any grinder + bean combination, the user can save a grind setting (free text field — could be "14 clicks," "3.5 rotations," "Setting 22," etc.)
- Optional notes field per combination (e.g., "Slightly coarser for iced")
- Grind Memory home screen shows the most recently used combinations at the top
- Quick-access: tapping a Grind Memory entry pre-loads its associated brew ratio and timer preset if linked

### Feature 4: Brew Log (Minimal)

- After a timer completes, user is prompted (not forced) to rate the brew on a simple 1–5 scale
- Optional one-line tasting note
- Log stores: date, bean, grinder, grind setting, ratio, total brew time, rating, note
- Log is viewable as a reverse-chronological list — no charts, no analytics in V1
- Purpose: helps user remember "did I like this setting last time?"

---

## 5. Feature Exclusion List (YAGNI)

These features are explicitly OUT OF SCOPE for V1. Do not build them. Do not add UI placeholders for them. They do not exist.

- **User accounts / cloud sync** — the #1 complaint about competitors. We never do this in V1.
- **Social features** — no sharing, no community feed, no "follow" system
- **Coffee bean marketplace or links** — we are not an e-commerce platform
- **Bluetooth scale integration** — cool but massively increases scope and BLE debugging complexity
- **Charts / analytics on brew history** — the log is a simple list, not a data dashboard
- **AI-powered taste recommendations** — gimmick, adds API dependency, breaks offline-first
- **iOS version** — Android only for V1
- **Tablet-optimized layouts** — phone-first, tablets get the phone layout
- **Widgets** — V2 candidate, not V1
- **Water temperature tracking** — nice to have, but not a core pain point worth adding UI for in V1
- **Coffee subscription tracking** — completely out of scope
- **Notifications / reminders to brew** — patronizing, nobody wants this

---

## 6. CLAUDE.md — Implementation AI Rules

> **Instructions for you (Eric):** Create a file called `CLAUDE.md` in the root of your Android Studio project. Paste the content below into it verbatim. Claude Code reads this file automatically and treats it as law.

```markdown
# CLAUDE.md — BrewMatrix Project Rules

## Architecture
- MVVM architecture with strict Unidirectional Data Flow (UDF)
- ZERO business logic in UI layer. All calculations, transformations, and data operations live in ViewModels or Repository classes
- UI layer only observes state and dispatches events

## State Management
- Use Kotlin StateFlow for all observable state in ViewModels
- Use `remember` and `mutableStateOf` with state hoisting in Compose
- Never use LiveData — StateFlow only

## Data Layer
- Room database for ALL persistent data
- Repository pattern: ViewModels never touch DAOs directly
- All database operations via Kotlin Coroutines (suspend functions)
- Database is the single source of truth — no in-memory-only state for persistent data

## UI Framework
- Jetpack Compose ONLY — no XML layouts, no Fragments, no Activities beyond the single MainActivity
- Material Design 3 (Material You) theming
- Single-Activity architecture with Jetpack Compose Navigation
- Support dark mode and dynamic color (Material You) from day one

## Code Style
- Kotlin only — no Java files
- One file per screen composable
- One file per ViewModel
- Entity, DAO, and Database classes each get their own file
- Use named arguments for any function call with more than 2 parameters
- Use data classes for all state objects

## Prohibited
- No XML layout files
- No deprecated Fragments
- No LiveData
- No Java files
- No Dagger/Hilt — use manual dependency injection via a simple AppContainer for V1
- No Retrofit or networking libraries — this app is offline-only
- No Firebase — no analytics, no crashlytics, no cloud anything in V1
- Do not add any dependency not explicitly listed in the build phase instructions

## Visual Design (MANDATORY — Read Section 3 of Project Bible)
- NEVER use default Roboto for display text or numbers. Bundle DM Sans + DM Mono as custom fonts in res/font/
- NEVER use pure white (#FFFFFF) as a screen background. Light theme background is warm off-white (#FAF7F2)
- NEVER use pure black (#000000) in dark theme. Background is warm charcoal (#121010)
- NEVER use default Material 3 purple/teal seed colors. The app uses a custom warm brown/amber/copper palette defined in the project bible
- NEVER create flat colored rectangle buttons. Primary action buttons (especially "Start Brew") MUST use Brush.horizontalGradient with the warm amber-to-brown gradient
- ALL numeric displays (timer, calculator) use the monospace font (DM Mono / JetBrains Mono) to prevent layout jitter
- ALL cards use RoundedCornerShape(16.dp) with soft warm shadows — not default Material elevation
- ALL state changes and screen transitions must be animated using Compose animation APIs (AnimatedVisibility, AnimatedContent, animateFloatAsState, etc.) — nothing appears or disappears instantly
- ALL list items use staggered fade-up animation on load (50ms delay per item)
- ALL number changes in the calculator use AnimatedContent with vertical slide transition, not instant text replacement
- Minimum touch targets: 48.dp for all tappable elements, 56.dp for buttons
- Minimum horizontal content padding: 16.dp. Vertical spacing between sections: 24.dp
- Use Material Symbols Rounded (filled) icons, never outlined style
- Chips are fully pill-shaped: RoundedCornerShape(24.dp)
- FAB uses colored shadow glow: shadow spotColor = AccentColor.copy(alpha = 0.25f)
```

---

## 7. Database Schema

The Room database is called `BrewMatrixDatabase`. It contains the following entities:

### Entity: `Grinder`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `name` | String | e.g., "Comandante C40" |
| `type` | String | e.g., "Hand Burr", "Electric Flat Burr" |
| `created_at` | Long | Epoch millis |

### Entity: `Bean`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `name` | String | e.g., "Yirgacheffe" |
| `roaster` | String (nullable) | e.g., "Onyx Coffee Lab" |
| `origin` | String (nullable) | e.g., "Ethiopia" |
| `created_at` | Long | Epoch millis |

### Entity: `GrindSetting`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `grinder_id` | Long (FK → Grinder) | |
| `bean_id` | Long (FK → Bean) | |
| `setting` | String | Free text: "14 clicks", "3.5", "Setting 22" |
| `notes` | String (nullable) | Optional user note |
| `linked_ratio_preset_id` | Long (nullable, FK → RatioPreset) | Optional link to a ratio |
| `linked_timer_preset_id` | Long (nullable, FK → TimerPreset) | Optional link to a timer |
| `last_used_at` | Long | Epoch millis — used for "recent" sorting |
| `created_at` | Long | Epoch millis |

**Unique constraint** on (`grinder_id`, `bean_id`) — one setting per combination. Updating overwrites.

### Entity: `RatioPreset`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `name` | String | e.g., "V60 Hoffmann" |
| `ratio` | Double | e.g., 16.667 (represents 1:16.667) |
| `is_default` | Boolean | True for presets that ship with the app |
| `sort_order` | Int | For manual ordering |

### Entity: `TimerPreset`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `name` | String | e.g., "V60 4-Phase" |
| `is_default` | Boolean | |
| `sort_order` | Int | |

### Entity: `TimerPhase`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `timer_preset_id` | Long (FK → TimerPreset) | |
| `phase_order` | Int | 0-indexed sequence |
| `name` | String | e.g., "Bloom", "First Pour" |
| `duration_seconds` | Int | |
| `target_water_grams` | Double (nullable) | Optional pour target for this phase |

### Entity: `BrewLog`

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto-generate) | |
| `bean_id` | Long (nullable, FK → Bean) | |
| `grinder_id` | Long (nullable, FK → Grinder) | |
| `grind_setting` | String (nullable) | Snapshot — not a FK, in case user edits the GrindSetting later |
| `ratio` | Double | The ratio used |
| `coffee_dose_grams` | Double | |
| `water_grams` | Double | |
| `total_brew_time_seconds` | Int | |
| `rating` | Int (nullable) | 1–5 scale |
| `note` | String (nullable) | One-line tasting note |
| `brewed_at` | Long | Epoch millis |

### DAOs Required

- `GrinderDao` — insert, update, delete, getAll, getById
- `BeanDao` — insert, update, delete, getAll, getById, search by name
- `GrindSettingDao` — upsert (insert or update on conflict), getByGrinderAndBean, getAllSortedByLastUsed, delete
- `RatioPresetDao` — insert, update, delete, getAll sorted by sort_order, getDefaults
- `TimerPresetDao` — insert, update, delete, getAll, getById (with phases via @Transaction)
- `TimerPhaseDao` — insert, update, delete, getByPresetId sorted by phase_order
- `BrewLogDao` — insert, getAll sorted by brewed_at desc, delete

---

## 8. App Architecture & Data Flow

```
┌──────────────────────────────────────────────────┐
│                   UI LAYER                        │
│  (Jetpack Compose Screens — observe state only)   │
│                                                    │
│  CalculatorScreen  TimerScreen  GrindMemoryScreen │
│  BrewLogScreen     SettingsScreen                  │
└────────────────────┬─────────────────────────────┘
                     │ State flows DOWN
                     │ Events flow UP
┌────────────────────▼─────────────────────────────┐
│                VIEWMODEL LAYER                     │
│  (All business logic, calculations, formatting)    │
│                                                    │
│  CalculatorViewModel   TimerViewModel              │
│  GrindMemoryViewModel  BrewLogViewModel            │
│  SettingsViewModel                                 │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│              REPOSITORY LAYER                      │
│  (Single access point to data — one Repository)    │
│                                                    │
│  BrewMatrixRepository                              │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│                DATA LAYER                          │
│  (Room Database + DAOs)                            │
│                                                    │
│  BrewMatrixDatabase                                │
│  GrinderDao, BeanDao, GrindSettingDao, etc.        │
└──────────────────────────────────────────────────┘
```

### Dependency Injection (Manual)

No Hilt/Dagger. Use a simple `AppContainer` object:

```
class AppContainer(context: Context) {
    val database = BrewMatrixDatabase.getInstance(context)
    val repository = BrewMatrixRepository(
        grinderDao = database.grinderDao(),
        beanDao = database.beanDao(),
        grindSettingDao = database.grindSettingDao(),
        ratioPresetDao = database.ratioPresetDao(),
        timerPresetDao = database.timerPresetDao(),
        timerPhaseDao = database.timerPhaseDao(),
        brewLogDao = database.brewLogDao()
    )
}
```

Created once in `Application` class, passed down via `CompositionLocalProvider` or ViewModel factory.

### Navigation

Single `NavHost` in `MainActivity` with these routes:

| Route | Screen |
|---|---|
| `calculator` | Brew Ratio Calculator (HOME / default) |
| `timer` | Multi-Phase Brew Timer |
| `timer/edit/{presetId}` | Edit Timer Preset Phases |
| `grind_memory` | Grind Memory Matrix |
| `grind_memory/add` | Add/Edit Grind Setting |
| `brew_log` | Brew Log History |
| `settings` | Settings + Tip Jar |

Bottom navigation bar with 4 tabs: Calculator, Timer, Grind Memory, Log. Settings accessible via gear icon in top app bar.

---

## 9. Screen-by-Screen UI Specification

> **CRITICAL DESIGN REMINDER:** Every screen described below must conform to the design system in Section 3. Read Section 3 in full before implementing ANY screen. If a screen looks like default Material Design with no custom personality, it is wrong. Regenerate it.

### 9.1 Calculator Screen (Home)

**Visual Design:**

- **Background:** Warm off-white (`#FAF7F2` light / `#121010` dark) — NOT pure white/black
- **Ratio preset chips:** Pill-shaped (`RoundedCornerShape(24.dp)`). Active chip uses a subtle horizontal gradient fill (amber → copper) with white text. Inactive chips use an outlined style with `Subtle Borders` color and `Secondary Text` label. Selection change animates: fill color fades in over 200ms, border fades out.
- **Numeric input fields:** Large, visually prominent cards with `16.dp` rounded corners and soft warm shadow. The number inside uses **DM Mono at 48–56sp**. The unit label ("g") is displayed in DM Sans at 16sp in `Secondary Text` color, right-aligned within the field. Fields have a very subtle warm gradient background (barely perceptible — `Surface` to `Surface + 2% warm tint`).
- **Ratio display between fields:** "1 : 16.667" displayed in DM Sans Medium at 16sp, `Secondary Text` color, centered. A thin decorative line (0.5dp, `Subtle Borders` color) extends from each side of the ratio text to the edges of the content area, creating a visual divider.
- **"Start Brew" button:** Full content-width. Height `56.dp`. **Horizontal gradient fill** (`Primary Gradient Start → Primary Gradient End`). Text in white, DM Sans Bold, 16sp. `RoundedCornerShape(16.dp)`. Soft warm shadow beneath (`elevation = 8.dp, spotColor = GradientEnd.copy(alpha = 0.3f)`). On press: scales to 0.96 with spring-back animation.
- **Number animation:** When the user edits one field and the other recalculates, the recalculated number uses `AnimatedContent` with a vertical slide transition (old value slides up and fades out, new value slides up and fades in). Duration: 150ms. This creates a smooth "rolling counter" effect.

**Layout:**

- **Top section:** Ratio selector — horizontally scrollable row of chips showing saved ratio presets. Active preset is filled, others are outlined. Long-press a chip to edit/delete. "+" chip at end to add new preset.
- **Middle section:** Two large numeric input fields stacked vertically.
  - Field 1: "Coffee" — dose in grams (editable)
  - Field 2: "Water" — yield in grams (editable)
  - Editing either field instantly recalculates the other based on the active ratio
  - Both fields show one decimal place (e.g., "18.0g" / "300.1g")
  - Between the two fields, display the active ratio as text: "1 : 16.667"
- **Bottom section:** Large "Start Brew" gradient button that navigates to the Timer screen, pre-loaded with the current dose/water values

**Behavior:**

- Default state on first launch: ratio 1:16.0, coffee dose 18.0g, water 288.0g
- If user has a linked timer preset from Grind Memory, auto-select it
- Numeric keyboard with decimal support opens when tapping either field
- No "calculate" button — calculation is live and instantaneous

### 9.2 Timer Screen

**Visual Design:**

- **Background:** Slightly darker than the standard screen background to create a focused, immersive "brewing mode" feel. In dark theme: `#0E0C0A`. In light theme: `#F2EDE5`.
- **Countdown display:** DM Mono / JetBrains Mono at **96sp minimum** in `Primary` color. Letter-spacing `+0.05em`. The seconds digit subtly pulses (scale 1.0 → 1.02 → 1.0) every tick — just enough to feel alive, not distracting. Use `Animatable` with spring physics.
- **Phase name:** ALL CAPS, DM Sans Bold, 14sp, `letterSpacing = 2.sp`, `Accent` color. Positioned directly above the countdown. Fades and slides when transitioning between phases.
- **Phase progress bar:** Height `4.dp`, full content width, `RoundedCornerShape(2.dp)`. Track: `Subtle Borders` color. Fill: **animated horizontal gradient** that shifts as progress increases — starts as a warm amber, ends as a rich copper when the phase is nearly complete. The fill edge has a subtle glow (achieved with a second, slightly wider, lower-opacity gradient behind).
- **Phase chips row:** Horizontal scrollable. Active phase chip uses gradient fill + white text. Completed phases use a muted filled style. Upcoming phases use outlined style. Phase transition animates: current chip shrinks with spring physics, next chip grows and gains the gradient fill.
- **Water target display:** Large text "Pour to 60g" — the number uses DM Mono at 32sp, the label uses DM Sans at 14sp. Displayed in a subtle card with warm tinted background. Only visible when the current phase has a target. Enters with `AnimatedVisibility` fade + slide from bottom.
- **Play/Pause button:** Center-bottom, large (`64.dp`). Uses gradient fill when in READY/PAUSED state (inviting the user to start). Uses a solid `Surface` fill with `Primary` icon when RUNNING (visually recedes so the timer dominates). Smooth icon morph between play ▶ and pause ⏸ shapes.
- **Reset button:** Smaller (`40.dp`), positioned to the side. Outlined style, `Secondary Text` color. Does not compete visually with Play/Pause.
- **Total elapsed time:** DM Mono at 14sp, `Secondary Text` color, bottom of screen. Subtle, informational, never dominant.
- **Brew completion state:** When the final phase ends, the entire background briefly pulses with a radial warm gold gradient (center outward, fades to transparent over 600ms). The countdown text transitions to display "Done" with a fade + scale-up animation. Then the brew log bottom sheet slides up.

**Layout:**

- **Center dominant:** Massive countdown display (96sp+ font, high contrast, monospace)
- **Phase indicator:** Current phase name displayed above the countdown (e.g., "BLOOM")
- **Phase progress bar:** Thin animated gradient bar showing progress within the current phase
- **Phase list:** Small horizontal row of phase chips below the timer showing all phases; active phase is highlighted
- **Water target:** If the current phase has a `target_water_grams`, display it prominently: "Pour to 60g"
- **Controls:** Play/Pause button (center, large), Reset button (smaller, to the side)
- **Total elapsed time:** Small secondary display showing total time since brew start

**Behavior:**

- On entering the screen, timer is paused showing Phase 1 ready to start
- User taps Play to begin
- At each phase transition: vibration haptic (100ms), short tone, auto-advance
- **WAKE LOCK ACTIVE** from the moment Play is tapped until the timer completes or is reset
- When final phase ends: longer haptic pattern, completion tone, warm glow animation, prompt to log the brew
- If user navigates away mid-brew, timer continues in a foreground notification (stretch goal — V1 can simply warn "leaving will stop the timer")

### 9.3 Grind Memory Screen

**Visual Design:**

- **Cards:** Each grind memory card is a `RoundedCornerShape(16.dp)` card with a very subtle radial gradient background (center slightly lighter than edges — creates depth). Soft warm shadow. Cards appear with staggered fade-up animation on screen load (each card delays 50ms after the previous).
- **Card content hierarchy:** Bean name in DM Sans Bold 16sp (Primary color). Grinder name in DM Sans Regular 14sp (Secondary Text). Grind setting displayed in a small pill badge with a muted warm background fill and DM Mono 14sp — this visually distinguishes the data point from the labels. Last used date in DM Sans Regular 12sp, Caption color, right-aligned.
- **"Brew This" action:** On each card, a small gradient-filled pill button reading "Brew" with a play icon. Uses the same gradient as the "Start Brew" button but at smaller scale. This is the quick-brew entry point.
- **FAB:** Warm gradient fill, not flat color. Rounded square shape (`RoundedCornerShape(20.dp)`), not circle. Colored shadow glow beneath. "+" icon in white.
- **Search bar:** Rounded (`RoundedCornerShape(12.dp)`), `Surface` background with subtle border, magnifying glass icon in `Secondary Text` color. When focused, border animates to `Accent` color.
- **Empty state:** Custom warm-toned illustration (simple line art of a coffee grinder with steam). DM Sans headline: "No beans in the memory yet." Secondary text: "Tap + to save your first grind setting." A gradient CTA button: "Add First Setting."
- **Swipe to delete:** Card slides right with opacity fade. Behind the card, a warm red (`Error` color) background reveals with a trash icon, fading in as the swipe progresses.

**Layout:**

- **Default view:** List of recent grind settings, sorted by `last_used_at` descending
- Each card shows: Bean name, Grinder name, Grind setting value, last used date
- Tapping a card opens detail view with edit capability and option to "Brew This" (jumps to Calculator pre-loaded with linked ratio)
- **FAB** (Floating Action Button): "+" to add a new grind setting
- **Add/Edit flow:** Step 1 — pick or create a grinder. Step 2 — pick or create a bean. Step 3 — enter grind setting text + optional notes. Step 4 — optionally link a ratio preset and timer preset. Save.

**Behavior:**

- If no grind settings exist yet, show an empty state with illustration and "Add your first grind setting" prompt
- Search/filter: simple text search bar at top that filters across bean name, grinder name, and setting text
- Swipe to delete with confirmation dialog

### 9.4 Brew Log Screen

**Visual Design:**

- **Log cards:** Same card styling as Grind Memory (rounded, warm shadow, subtle gradient background). Rating displayed as 1–5 filled/unfilled coffee bean icons in `Accent` color — these are small custom SVG/Canvas-drawn icons, NOT text emoji.
- **Expand/collapse:** Tapping a card smoothly expands it using `AnimatedVisibility` with expand + fade. The expanded detail section slides in from top with a brief stagger. Collapse reverses the animation.
- **Date headers:** When multiple brews exist, group by date with sticky headers in DM Sans Medium 12sp, all caps, `Secondary Text` color, `letterSpacing = 1.sp`.
- **Empty state:** Illustration of a steaming coffee cup. Headline: "No brews logged yet." Secondary: "Complete a timer session to log your first brew."

**Layout:**

- Reverse-chronological list of past brews
- Each card shows: date, bean name (or "Unknown"), rating (1–5 as filled/unfilled coffee bean icons), ratio, total time
- Tapping expands inline to show grinder, grind setting, dose, water, and tasting note
- No charts, no graphs, no analytics

**Behavior:**

- Empty state: "No brews logged yet. Complete a timer session to log your first brew."
- Swipe to delete with confirmation

### 9.5 Settings Screen

**Visual Design:**

- **Grouped sections:** Settings are organized in rounded cards per group (Brewing Defaults, Appearance, Support, Data, About). Each group card has the standard warm shadow and rounded corners.
- **Tip Jar buttons:** Three horizontally arranged gradient buttons, each with increasing visual richness. "Espresso ($1.99)" uses a subtle short gradient. "Pour-Over ($4.99)" uses a medium warm gradient. "Bag of Beans ($9.99)" uses the full rich gold-to-brown gradient with a slightly larger shadow. This visual escalation subtly encourages the higher tiers.
- **Theme preview:** When the user taps a theme option, show a small inline preview card that animates to display the selected theme's colors. This gives immediate feedback before applying.
- **Cosmetic upgrade cards:** "Premium Themes" and "Premium App Icons" each displayed as a horizontal card with a small preview mosaic of the unlockable content on the left, description text on the right, and a price pill button. If already purchased, show a subtle checkmark badge and "Unlocked" text in the `Secondary` accent color.

**Layout, in order from top to bottom:**

- **Default ratio:** Set the default ratio for the calculator on app launch
- **Default dose:** Set the default coffee dose on app launch
- **Units:** Grams only for V1 (display this as non-interactive, just "Grams" — future-proofing for oz support)
- **Theme:** System default / Light / Dark
- **Tip Jar section header:** "Support BrewMatrix"
  - Brief friendly text: "BrewMatrix is free with no ads, forever. If it's improved your morning routine, consider buying me a coffee."
  - Three tip buttons: "Espresso ($1.99)" / "Pour-Over ($4.99)" / "Bag of Beans ($9.99)"
- **Cosmetic Upgrades section header:** "Customize"
  - "Premium Themes" — unlock pack: AMOLED Black, Ocean Blue, Warm Latte ($2.99 one-time)
  - "Premium App Icons" — unlock pack: alternate launcher icons ($2.99 one-time)
- **About:** App version, "Made with caffeine and obsession", link to privacy policy
- **Data:** "Export Brew Log as CSV", "Clear All Data" (with double confirmation)

---

## 10. Brew Timer Logic

### Timer State Machine

```
READY → RUNNING → PHASE_TRANSITION → RUNNING → ... → COMPLETED
  ↑        ↓                                           ↓
  ←←←← PAUSED ←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←
  ↑                                                    ↓
  ←←←←←←←←←←←←←←←←← RESET ←←←←←←←←←←←←←←←←←←←←←←←←
```

### Timer ViewModel State

```kotlin
data class TimerUiState(
    val timerState: TimerState = TimerState.READY,
    val phases: List<TimerPhase> = emptyList(),
    val currentPhaseIndex: Int = 0,
    val currentPhaseRemainingMillis: Long = 0L,
    val totalElapsedMillis: Long = 0L,
    val coffeeGrams: Double = 0.0,
    val waterGrams: Double = 0.0,
    val ratio: Double = 0.0
)

enum class TimerState { READY, RUNNING, PAUSED, PHASE_TRANSITION, COMPLETED }
```

### Wake Lock Management

- Acquire `PowerManager.SCREEN_BRIGHT_WAKE_LOCK` when `TimerState` transitions to `RUNNING`
- Release wake lock when `TimerState` transitions to `COMPLETED`, `READY`, or when the composable is disposed
- **Critical:** Always release in `onCleared()` of the ViewModel AND in a `DisposableEffect` in the composable to prevent battery drain if the user force-closes the app

### Audio/Haptic Cues

- Phase transition: `VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)` + system notification sound
- Timer complete: `VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200, 100, 200), -1)` + distinct completion tone

---

## 11. Grind Memory Matrix Logic

### The Core Concept

The Grind Memory is a lookup table indexed by two keys: **Grinder** and **Bean**. For every unique (Grinder, Bean) pair, the user stores exactly one grind setting.

### Upsert Behavior

When a user saves a grind setting for a Grinder+Bean combo that already exists, it **overwrites** the previous setting (Room `@Insert(onConflict = OnConflictStrategy.REPLACE)` on the unique constraint). The `last_used_at` timestamp updates to now.

### Quick-Brew Flow

1. User taps a Grind Memory card
2. App checks if the card has `linked_ratio_preset_id` and `linked_timer_preset_id`
3. If both are set: navigates to Calculator with ratio pre-loaded, then user can tap "Start Brew" to go to Timer with phases pre-loaded
4. If only ratio is linked: navigates to Calculator with ratio only
5. If neither: navigates to Calculator with defaults

This makes the Grind Memory the true "home base" for repeat brewers — one tap to recall everything about how they brew a specific bean.

---

## 12. Monetization Implementation

### Tip Jar (Google Play In-App Purchases)

Use RevenueCat SDK to simplify IAP integration. Three consumable products:

| Product ID | Display Name | Price |
|---|---|---|
| `tip_espresso` | Espresso | $1.99 |
| `tip_pourover` | Pour-Over | $4.99 |
| `tip_beans` | Bag of Beans | $9.99 |

These are **consumable** products — users can tip multiple times.

After a successful purchase, show a brief, warm thank-you animation (coffee cup filling up) and a toast: "Thanks for the coffee! ☕"

### Cosmetic Upgrades (Non-Consumable IAP)

| Product ID | Display Name | Price |
|---|---|---|
| `premium_themes` | Premium Theme Pack | $2.99 |
| `premium_icons` | Premium Icon Pack | $2.99 |

These are **non-consumable** — purchased once, unlocked forever.

Store unlock state locally via Room (a simple `PurchaseState` entity with product ID and boolean) AND verify via RevenueCat's entitlement system.

### What's Free vs. Paid

| Feature | Free | Paid |
|---|---|---|
| Ratio calculator with decimal precision | ✓ | — |
| Multi-phase brew timer with wake lock | ✓ | — |
| Grind Memory (unlimited entries) | ✓ | — |
| Brew Log | ✓ | — |
| All default themes (light, dark, system) | ✓ | — |
| Premium themes (AMOLED, Ocean, Latte) | — | $2.99 |
| Premium app icons | — | $2.99 |

**Rule: Never lock a functional feature behind a paywall.** The paid items are purely cosmetic.

---

## 13. Sequential Build Phases

> **Instructions for you (Eric):** Execute these phases one at a time with Claude Code. Do NOT skip ahead. Confirm a clean build after each phase before starting the next one. Copy-paste the relevant phase into Claude Code as your instruction.

### Phase 1: Project Setup & Dependencies

**Instruction to give Claude Code:**

> Create a new Android project called BrewMatrix with package name `com.brewmatrix.app`. Minimum SDK 26, target SDK 35, Kotlin 2.0+. Set up the build.gradle.kts files with the following dependencies:
>
> - `androidx.compose.material3` (latest stable BOM)
> - `androidx.navigation:navigation-compose`
> - `androidx.room:room-runtime` and `room-ktx`
> - KSP plugin for Room annotation processing
> - `androidx.lifecycle:lifecycle-viewmodel-compose`
> - `androidx.compose.material:material-icons-extended`
> - `androidx.compose.animation:animation` (for AnimatedContent, AnimatedVisibility)
>
> Download and bundle DM Sans (Regular, Medium, Bold) and DM Mono (Regular, Medium) font files into `res/font/`. Create a custom Compose Typography object using these fonts: DM Sans for all body/label/title styles, DM Mono for display/headline styles (used for numbers).
>
> Create a custom Material 3 color scheme — NOT the default seed color. Use these exact colors for light theme: Background #FAF7F2, Surface #FFFFFF, Primary #3C2415, Secondary #7A9E7E, Tertiary #B8860B, OnBackground #1A1A1A. For dark theme: Background #121010, Surface #1E1B18, Primary #D4A05A, Secondary #8FAF8A, Tertiary #D4944A, OnSurface #F0EBE3. Also define custom color values for gradients: GradientStart (#C17D3A light / #E8B86D dark), GradientEnd (#6B3A1F light / #A0693D dark), SubtleBorder (#E8E2DA light / #2E2926 dark).
>
> Create a blank MainActivity that displays "BrewMatrix" centered on screen using this custom theme. Ensure it compiles and runs and respects light/dark mode with the custom colors.

**Success criteria:** App compiles, launches on device, shows "BrewMatrix" in DM Sans on a warm off-white (not pure white) background, dark mode shows warm charcoal (not pure black), custom fonts render correctly.

### Phase 2: Database Layer

**Instruction to give Claude Code:**

> Following the database schema in the project bible (reference the schema section), create all Room entities, DAOs, and the BrewMatrixDatabase class. Create a BrewMatrixRepository class that wraps all DAOs. Create an AppContainer class for manual dependency injection. Wire AppContainer into a custom Application class. Pre-populate the database with these default RatioPresets on first launch:
>
> - "V60 Classic" = 1:15.0
> - "V60 Hoffmann" = 1:16.667
> - "Chemex" = 1:17.0
> - "AeroPress" = 1:12.0
> - "French Press" = 1:15.0
> - "Kalita Wave" = 1:16.0
>
> And these default TimerPresets with phases:
>
> - "V60 Standard" — Bloom 45s, First Pour 30s, Second Pour 30s, Drawdown 90s
> - "AeroPress Standard" — Brew 60s, Press 30s
> - "French Press 4-Min" — Bloom 30s, Steep 210s, Press 15s
>
> Do not build any UI beyond what exists. Just ensure the database initializes correctly and the app still compiles.

**Success criteria:** App compiles and launches. Database creates on first run (verify via App Inspection in Android Studio or log output).

### Phase 3: Calculator ViewModel + Screen

**Instruction to give Claude Code:**

> Build the CalculatorViewModel and CalculatorScreen. The ViewModel holds: active ratio (Double), coffee dose (Double), water yield (Double), and a list of RatioPresets from the repository. Changing coffee dose recalculates water. Changing water recalculates dose. Changing ratio recalculates water based on current dose.
>
> READ THE PROJECT BIBLE SECTION 3 (Design Language) AND SECTION 9.1 (Calculator Visual Design) BEFORE WRITING ANY UI CODE. Follow every visual specification exactly:
>
> - Warm off-white background (#FAF7F2 light / #121010 dark), NOT pure white or black
> - Ratio preset chips are pill-shaped (RoundedCornerShape(24.dp)). Active chip uses horizontal gradient fill (GradientStart → GradientEnd). Inactive chips are outlined. Selection animates with a 200ms fill fade.
> - Numeric input fields are large cards (RoundedCornerShape(16.dp), soft warm shadow). Numbers displayed in DM Mono at 48-56sp. Unit label "g" in DM Sans 16sp, Secondary Text color.
> - Ratio text "1 : 16.667" centered between fields in DM Sans Medium 16sp with decorative divider lines extending to content edges.
> - "Start Brew" button: full-width, 56dp height, horizontal gradient fill (GradientStart → GradientEnd), white text in DM Sans Bold 16sp, RoundedCornerShape(16.dp), warm shadow with gradient-tinted spotColor. On press: scale to 0.96 with spring-back.
> - Number changes use AnimatedContent with vertical slide transition (old value slides up + fades out, new value slides up + fades in, 150ms). Numbers must NEVER just snap to a new value.
> - All list items (chips) animate in with staggered fade-up on screen load.

**Success criteria:** Calculator works correctly with decimal ratios. Numbers animate smoothly when recalculating. Gradient button renders with warm shadow. Chips animate on selection. Custom fonts (DM Mono for numbers, DM Sans for labels) are visible. Background is warm-tinted, not pure white/black.

### Phase 4: Timer ViewModel + Screen

**Instruction to give Claude Code:**

> Build the TimerViewModel and TimerScreen. The ViewModel manages the timer state machine (READY, RUNNING, PAUSED, PHASE_TRANSITION, COMPLETED). It loads phases from a TimerPreset and counts down each phase sequentially.
>
> Implement Android wake lock: acquire SCREEN_BRIGHT_WAKE_LOCK when timer starts running, release when completed or reset. Always release in ViewModel onCleared().
>
> Implement haptic feedback at phase transitions and a distinct pattern at completion.
>
> READ THE PROJECT BIBLE SECTION 3 (Design Language) AND SECTION 9.2 (Timer Visual Design) BEFORE WRITING ANY UI CODE. Follow every visual specification exactly:
>
> - Timer background is slightly darker than standard screens (#F2EDE5 light / #0E0C0A dark) to create an immersive "brewing mode" feel.
> - Countdown display: DM Mono at 96sp minimum, Primary color. Seconds digit subtly pulses (scale 1.0 → 1.02 → 1.0) every tick using Animatable with spring physics. Letter-spacing +0.05em.
> - Phase name: ALL CAPS, DM Sans Bold 14sp, letterSpacing = 2.sp, Accent color. Fades and slides on transition.
> - Phase progress bar: 4.dp height, full width. Track in SubtleBorder color. Fill uses animated horizontal gradient that shifts warmer as progress increases (amber → copper). Add a second slightly wider lower-opacity gradient behind the fill edge for a glow effect.
> - Phase chips: horizontal scroll. Active chip = gradient fill + white text. Completed = muted fill. Upcoming = outlined. Transitions use spring physics (dampingRatio = 0.6f, stiffness = 400f).
> - Water target "Pour to 60g": number in DM Mono 32sp, label in DM Sans 14sp, inside a subtle warm-tinted card. Enters with AnimatedVisibility fade + slide from bottom.
> - Play/Pause button: 64dp, gradient fill when READY/PAUSED, solid Surface fill with Primary icon when RUNNING. Smooth icon morph between play and pause shapes.
> - Reset button: 40dp, outlined, Secondary Text color. Visually secondary.
> - Total elapsed time: DM Mono 14sp, Secondary Text color, bottom of screen.
> - Brew completion: radial warm gold gradient pulse from center outward (600ms, EaseOut), then countdown text transitions to "Done" with fade + scale-up animation. Then brew log bottom sheet slides up.
>
> Wire the "Start Brew" button from the Calculator screen to navigate to the Timer with the selected timer preset.

**Success criteria:** Timer counts down through phases with animated gradient progress bar, auto-advances with spring-animated chip transitions, wake lock keeps screen on, seconds digit pulses subtly, haptic feedback fires at transitions, play/pause/reset all work correctly, completion triggers warm glow animation. Dark and light themes both use the slightly darker immersive background.

### Phase 5: Grind Memory ViewModel + Screens

**Instruction to give Claude Code:**

> Build the GrindMemoryViewModel and the Grind Memory screens: a list screen showing recent grind settings, and an add/edit screen.
>
> READ THE PROJECT BIBLE SECTION 3 (Design Language) AND SECTION 9.3 (Grind Memory Visual Design) BEFORE WRITING ANY UI CODE. Follow every visual specification exactly:
>
> - Cards: RoundedCornerShape(16.dp) with subtle radial gradient background (center slightly lighter than edges). Soft warm shadow. Cards appear with staggered fade-up animation on load (each card delays 50ms after previous using LaunchedEffect with incremental delay per item index).
> - Card content: Bean name in DM Sans Bold 16sp (Primary). Grinder name in DM Sans Regular 14sp (Secondary Text). Grind setting in a small pill badge with muted warm background + DM Mono 14sp. Last used date in DM Sans 12sp, Caption color, right-aligned.
> - Each card has a small "Brew" pill button with gradient fill and play icon — this is the quick-brew entry point.
> - FAB: gradient fill (GradientStart → GradientEnd), RoundedCornerShape(20.dp) not circle, colored shadow glow (spotColor = AccentColor.copy(alpha = 0.25f)), white "+" icon.
> - Search bar: RoundedCornerShape(12.dp), Surface background, subtle border. On focus, border animates to Accent color.
> - Empty state: custom warm-toned line art illustration of a coffee grinder with steam (draw using Compose Canvas or include as SVG vector drawable). Headline: "No beans in the memory yet" in DM Sans. Secondary text below. Gradient CTA button: "Add First Setting."
> - Swipe to delete: card slides right with opacity fade, warm red (Error color) background reveals with trash icon fading in.
>
> Add/edit screen: step flow — pick or create grinder, pick or create bean, enter grind setting text, optional notes, optionally link a ratio preset and timer preset. Save performs an upsert.
>
> Implement the Quick-Brew flow: tapping a grind memory card's "Brew" button navigates to Calculator pre-loaded with linked ratio.

**Success criteria:** Cards render with warm gradients and custom fonts, staggered animation plays on load, FAB has gradient fill with glow shadow, empty state shows illustration with gradient CTA, search bar border animates on focus, swipe-to-delete reveals red background. Quick-brew navigation works.

### Phase 6: Brew Log + Post-Timer Logging

**Instruction to give Claude Code:**

> Build the BrewLogViewModel and BrewLogScreen. After the timer reaches COMPLETED state, show a bottom sheet prompting the user to rate the brew (1–5, using custom coffee bean icons drawn with Compose Canvas, NOT text emoji) and add an optional one-line note. Save the log entry with all brew parameters.
>
> READ THE PROJECT BIBLE SECTION 3 AND SECTION 9.4 (Brew Log Visual Design) BEFORE WRITING ANY UI CODE:
>
> - Bottom sheet: RoundedCornerShape(24.dp) top corners. Frosted-glass effect at top edge — a thin gradient from Surface.copy(alpha = 0.0f) to Surface over 24dp. Rating coffee bean icons use Accent color for filled, SubtleBorder color for unfilled.
> - Log cards: same warm styling as Grind Memory cards (rounded, warm shadow, subtle gradient). Staggered fade-up on load.
> - Expand/collapse: uses AnimatedVisibility with expand + fade. Expanded detail section slides in from top with brief stagger.
> - Date headers: group by date with sticky headers — DM Sans Medium 12sp, ALL CAPS, Secondary Text color, letterSpacing = 1.sp.
> - Empty state: illustration of steaming coffee cup (Canvas or vector). "No brews logged yet." + "Complete a timer session to log your first brew."
>
> Swipe to delete with confirmation.

**Success criteria:** Completing a timer session shows the styled bottom sheet with custom coffee bean rating icons. Logs appear with staggered animation, expand/collapse animates smoothly, date headers group entries. Empty state shows illustration.

### Phase 7: Settings Screen + Navigation Bar

**Instruction to give Claude Code:**

> Build the SettingsViewModel and SettingsScreen.
>
> READ THE PROJECT BIBLE SECTION 3 AND SECTION 9.5 (Settings Visual Design) BEFORE WRITING ANY UI CODE:
>
> - Organize settings into grouped rounded cards per section (Brewing Defaults, Appearance, Support, Data, About). Each group card has standard warm shadow and rounded corners (16.dp).
> - Tip Jar buttons: three horizontally arranged gradient buttons with escalating visual richness — "Espresso" has a subtle short gradient, "Pour-Over" a medium warm gradient, "Bag of Beans" the full rich gold-to-brown gradient with a slightly larger shadow. This visual escalation subtly encourages higher tiers.
> - Theme selector: show a small inline preview card that animates to display selected theme colors when tapped.
> - Cosmetic upgrade cards: horizontal layout with small preview mosaic on the left, description text on the right, price pill button. If already purchased, show checkmark badge + "Unlocked" text.
>
> Settings include: default ratio, default dose, theme selector (System/Light/Dark), About section, and data management (Export CSV, Clear All Data with double confirmation).
>
> Implement the bottom navigation bar with 4 tabs: Calculator, Timer, Grind Memory, Log. The nav bar has a subtle vertical gradient background (Surface → slightly darker) to create a sense of grounded weight. Use Material Symbols Rounded (filled) icons, not outlined. Settings accessible via a gear icon in the top app bar.
>
> Screen transitions between tabs use shared axis animation (horizontal slide + fade, 300ms, EaseInOutCubic).
>
> Implement theme switching that persists across app restarts (save preference in DataStore or Room).
>
> Leave the Tip Jar and Cosmetic Upgrades sections as UI placeholders with "Coming Soon" text — IAP integration is Phase 8.

**Success criteria:** All navigation works with animated transitions. Bottom nav has gradient background with filled rounded icons. Settings screen shows grouped cards with proper warm styling. Theme switching persists and correctly applies the custom warm color schemes (not default Material purple/teal). CSV export produces a valid file. Clear data works with double confirmation.

### Phase 8: Monetization (RevenueCat + IAP)

> **Note:** This phase requires a Google Play Console developer account and RevenueCat account. Set those up before starting this phase.

**Instruction to give Claude Code:**

> Add the RevenueCat SDK dependency (`com.revenuecat.purchases:purchases`). Initialize RevenueCat in the Application class.
>
> In the Settings screen, replace the "Coming Soon" placeholders with functional IAP buttons. Tip Jar: three consumable products. Cosmetic Upgrades: two non-consumable products. On successful tip purchase, show a thank-you toast. On successful theme/icon purchase, unlock the content immediately.
>
> Create a PurchaseState entity in Room to cache unlock state locally for offline access. Verify entitlements via RevenueCat on app launch when online.
>
> Implement the three premium themes (AMOLED Black, Ocean Blue, Warm Latte) and wire them to the theme selector, gated behind the purchase check.

**Success criteria:** IAP flow works in Google Play testing sandbox. Purchased themes unlock and persist. Tips process correctly.

---

## 14. Testing Checklist

Test every item below on a physical device before publishing.

### Calculator
- [ ] Entering coffee dose recalculates water correctly
- [ ] Entering water yield recalculates dose correctly
- [ ] Changing ratio recalculates water based on current dose
- [ ] Ratio of 1:16.667 produces correct results (18g coffee = 300.006g water)
- [ ] Edge case: 0g dose shows 0g water
- [ ] Edge case: extremely large numbers don't crash (999g)
- [ ] Decimal input works (18.5g)
- [ ] Switching between ratio presets recalculates instantly

### Timer
- [ ] Timer counts down correctly (1 second = 1 second of real time)
- [ ] Phase auto-advance works
- [ ] Haptic fires at phase transitions
- [ ] Wake lock keeps screen on during entire brew
- [ ] Wake lock releases when timer completes
- [ ] Wake lock releases when timer is reset
- [ ] Wake lock releases if app is force-closed
- [ ] Pause and resume work correctly (no time drift)
- [ ] Phase water targets display correctly
- [ ] Total elapsed time is accurate

### Grind Memory
- [ ] Can create a new grinder
- [ ] Can create a new bean
- [ ] Can save a grind setting for a grinder+bean combo
- [ ] Saving the same grinder+bean combo overwrites (upsert)
- [ ] Recent entries appear at top
- [ ] Search filters correctly
- [ ] Quick-brew navigates to Calculator with correct ratio
- [ ] Delete works with confirmation

### Brew Log
- [ ] Log prompt appears after timer completion
- [ ] Rating saves correctly (1–5)
- [ ] Note saves correctly
- [ ] Log entries display in reverse chronological order
- [ ] Expand/collapse shows full details
- [ ] Delete works with confirmation

### General
- [ ] App works entirely offline
- [ ] Dark mode works
- [ ] Light mode works
- [ ] Dynamic color (Material You) works
- [ ] No crashes on rotation
- [ ] Back navigation works correctly from every screen
- [ ] First launch with empty database shows appropriate empty states

### Visual Design Quality (MANDATORY — test on physical device)
- [ ] Background is warm off-white (#FAF7F2) in light mode, NOT pure white
- [ ] Background is warm charcoal (#121010) in dark mode, NOT pure black
- [ ] DM Sans font renders on all labels, headers, and body text — NOT default Roboto
- [ ] DM Mono font renders on all numeric displays (calculator, timer, grind settings) — NOT default Roboto
- [ ] "Start Brew" button shows horizontal warm gradient, NOT a flat colored rectangle
- [ ] "Start Brew" button has visible warm shadow beneath it
- [ ] "Start Brew" button scales down on press with spring-back animation
- [ ] Calculator number changes animate with vertical slide transition, NOT instant replacement
- [ ] Timer countdown digit subtly pulses each second
- [ ] Timer phase progress bar shows animated gradient fill (amber → copper)
- [ ] Timer phase transition animates chips with spring physics
- [ ] Timer completion triggers radial warm glow pulse animation
- [ ] Grind Memory cards appear with staggered fade-up animation on screen load
- [ ] Grind Memory FAB has gradient fill and colored shadow glow, NOT flat color
- [ ] Bottom navigation bar has subtle gradient background, NOT flat surface
- [ ] Bottom nav icons are filled/rounded style, NOT outlined
- [ ] All screen transitions animate (horizontal slide + fade), NOT instant
- [ ] All cards have rounded corners (16dp) with soft warm shadows, NOT sharp edges or harsh shadows
- [ ] Chip selection animates (fill fade in/out), NOT instant
- [ ] Empty states show warm-toned illustrations with gradient CTA buttons, NOT just plain text
- [ ] No pure #FFFFFF or #000000 visible anywhere in light/dark mode (except AMOLED premium theme)
- [ ] No default Material purple, teal, or blue tint visible anywhere

---

## 15. App Store Optimization (ASO)

### Title
BrewMatrix — Coffee Ratio & Grind Journal

### Short Description (80 chars)
Precision brew calculator, timer & grind memory. Offline. No ads. No account.

### Full Description (Key Points to Hit)

- Exact non-integer ratios (1:16.667 — not just whole numbers)
- Multi-phase brew timer that keeps your screen on
- Grind Memory: save your grind settings per bean per grinder
- Works 100% offline — no account, no cloud, no sign-up
- Zero ads, forever
- Supports V60, Chemex, AeroPress, French Press, Kalita Wave, and any custom method
- Material You design with dark mode support
- Brew log to track your favorites
- Made by a coffee nerd, for coffee nerds

### Keywords to Target
`coffee ratio calculator, brew timer, pour over timer, grind settings, V60 timer, specialty coffee app, coffee journal, brew ratio, coffee dose calculator, AeroPress timer`

### Category
Food & Drink

### Content Rating
Everyone

---

## 16. Post-Launch Roadmap

These are candidates for future versions, prioritized by user value. Do not build any of these in V1.

| Priority | Feature | Version Target |
|---|---|---|
| 1 | Home screen widget (quick-start last brew) | V1.1 |
| 2 | CSV import for grind settings (migration from spreadsheets) | V1.1 |
| 3 | Backup/restore via local file (no cloud, user exports a .json) | V1.2 |
| 4 | Water temperature field in brew log | V1.2 |
| 5 | Brew log charts (extraction trends over time) | V2.0 |
| 6 | Bluetooth scale integration (Acaia, Felicita) | V2.0 |
| 7 | WearOS companion (timer on watch) | V2.0+ |
| 8 | iOS version via KMP | V3.0 |

---

## Appendix: Default Preset Data

### Default Ratio Presets

| Name | Ratio | Sort Order |
|---|---|---|
| V60 Classic | 15.0 | 0 |
| V60 Hoffmann | 16.667 | 1 |
| Chemex | 17.0 | 2 |
| AeroPress | 12.0 | 3 |
| French Press | 15.0 | 4 |
| Kalita Wave | 16.0 | 5 |

### Default Timer Presets

**V60 Standard**

| Phase | Name | Duration (s) | Water Target (g) |
|---|---|---|---|
| 0 | Bloom | 45 | 60 |
| 1 | First Pour | 30 | 180 |
| 2 | Second Pour | 30 | 300 |
| 3 | Drawdown | 90 | — |

**AeroPress Standard**

| Phase | Name | Duration (s) | Water Target (g) |
|---|---|---|---|
| 0 | Brew | 60 | 200 |
| 1 | Press | 30 | — |

**French Press 4-Minute**

| Phase | Name | Duration (s) | Water Target (g) |
|---|---|---|---|
| 0 | Bloom | 30 | 60 |
| 1 | Steep | 210 | — |
| 2 | Press | 15 | — |

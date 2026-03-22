# BrewMatrix Design System Reference

Read this file before creating or modifying ANY UI composable, theme file, or visual component. Every rule here is mandatory. If generated output violates any rule, fix it before committing.

## Design Philosophy: "Precision Craft"

The app must look premium — like high-end coffee packaging meets a Swiss-designed instrument. Warm, intentional, precise, elevated. Never generic, flat, or stock Material Design.

Four principles govern every visual decision:
- **Warm Luxury** — warm tones, rich surfaces, tactile depth (think Fellow Stagg packaging, not a spreadsheet)
- **Confident Restraint** — few elements, all visually refined, generous whitespace
- **Depth & Dimension** — layered surfaces with shadows, gradients, translucency (not flat rectangles)
- **Motion With Purpose** — every state change animates; nothing appears or disappears instantly

---

## Color System

Do NOT use default Material 3 color seeds. Build the custom system below.

### Light Theme — "Morning Brew"

| Role | Hex | Usage |
|---|---|---|
| Background | `#FAF7F2` | Warm off-white. NEVER pure `#FFFFFF`. |
| Surface / Cards | `#FFFFFF` | Cards sit on warm background with warm shadow |
| Primary | `#3C2415` | Deep espresso brown. Headers, key text. |
| Gradient Start | `#C17D3A` | Warm amber. Left/top of gradient buttons. |
| Gradient End | `#6B3A1F` | Deep roast. Right/bottom of gradients. |
| Secondary | `#7A9E7E` | Muted sage green. Organic accent. |
| Accent / Interactive | `#B8860B` | Copper gold. FABs, active states, highlights. |
| On-Background Text | `#1A1A1A` | Near black. NOT pure `#000000`. |
| Secondary Text | `#6B6560` | Warm gray. Labels, timestamps. |
| Subtle Borders | `#E8E2DA` | Dividers, card outlines. |
| Error | `#C0392B` | Warm red. |

### Dark Theme — "Late Night Pour"

| Role | Hex | Usage |
|---|---|---|
| Background | `#121010` | Warm charcoal. NOT pure `#000000`. |
| Surface / Cards | `#1E1B18` | Slight warmth. Not cold gray. |
| Primary | `#D4A05A` | Warm gold against dark. |
| Gradient Start | `#E8B86D` | Bright amber. |
| Gradient End | `#A0693D` | Burnished copper. |
| Secondary | `#8FAF8A` | Soft olive. |
| Accent / Interactive | `#D4944A` | Bright copper. |
| On-Surface Text | `#F0EBE3` | Warm white. NOT pure `#FFFFFF`. |
| Secondary Text | `#9C9489` | Muted tan. |
| Subtle Borders | `#2E2926` | Barely visible structure. |
| Error | `#E74C3C` | Warm salmon. |

### Premium Themes (Paid Unlocks)

**AMOLED Black:** Background `#000000`, gold accents `#D4A05A`, text `#F0EBE3`.
**Ocean Blue:** Background `#0A1628`, primary `#4A90D9`, accent `#6DBAE0`.
**Warm Latte:** Background `#F5EDE0`, primary `#8B6F4E`, accent `#D4B896`.

---

## Typography

Bundle these fonts in `res/font/`. No runtime Google Fonts.

| Role | Font | Weight | Size | Notes |
|---|---|---|---|---|
| Timer Countdown | DM Mono / JetBrains Mono | Medium | 72–96sp | Monospace prevents digit shift during countdown |
| Large Numbers (Calculator) | DM Mono / JetBrains Mono | Regular | 48–56sp | Same monospace for all numeric displays |
| Section Headers | DM Sans | Bold | 20–24sp | Clean geometric sans |
| Body / Labels | DM Sans | Regular | 14–16sp | |
| Chip / Tag Text | DM Sans | Medium | 12–14sp | |
| Captions | DM Sans | Regular | 12sp | Muted color |

Rules:
- Timer countdown letter spacing: `+0.05em`
- Active timer phase labels: ALL CAPS with `letterSpacing = 2.sp`
- All other text: sentence case or title case, never all-caps

---

## Gradients

Gradients are structural design elements, not decoration.

### Where to use gradients:
- **"Start Brew" button:** Full-width horizontal gradient (Gradient Start → Gradient End), `RoundedCornerShape(16.dp)`, subtle inner shadow for physical depth
- **Timer phase progress bar:** Animated gradient shifts warmer as phase progresses
- **Bottom nav background:** Subtle vertical gradient from Surface to slightly darker
- **Grind Memory cards:** Subtle radial gradient (center-light to edge-slightly-darker)
- **Tip Jar buttons:** Visual richness scales with tip amount (small tip = subtle gradient, big tip = rich gold-to-brown)
- **Timer completion:** Radial warm gold pulse from center outward (600ms)

### Where NOT to use gradients:
- Body text or small UI elements
- Bottom nav icons (keep solid)
- Never rainbow or multi-hue — stay within warm amber/brown/copper family

### Implementation pattern:
```kotlin
Modifier.background(
    brush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    ),
    shape = RoundedCornerShape(16.dp)
)
```

---

## Elevation, Shadows & Depth

- **Cards:** `tonalElevation = 2.dp` + `Modifier.shadow(elevation = 8.dp, spotColor = Color(0x15000000), shape = RoundedCornerShape(16.dp))`. Warm, diffuse shadows.
- **FAB:** Shadow `12.dp` with accent-tinted spot color: `spotColor = AccentColor.copy(alpha = 0.25f)`. Creates colored glow.
- **Bottom sheets:** Frosted-glass top edge — gradient from `Surface.copy(alpha = 0.0f)` to `Surface` over `24.dp`.
- **Active/pressed states:** Briefly increase shadow depth + shift shadow color warmer.

---

## Corner Radii

| Element | Radius |
|---|---|
| Cards | `16.dp` |
| Large buttons | `16.dp` |
| Chips / Tags | `24.dp` (pill-shaped) |
| Input fields | `12.dp` |
| Bottom sheet | `24.dp` top corners only |
| FAB | `20.dp` (rounded square, not circle) |
| Bottom nav bar | `0.dp` (flush to edges) |

---

## Animations

Every state change must animate. Nothing instant.

| Interaction | How | Duration | Easing |
|---|---|---|---|
| Screen navigation | Horizontal slide + fade | 300ms | EaseInOutCubic |
| Card list load | Staggered fade-up, 16dp slide, 50ms stagger per item | 250ms each | EaseOutCubic |
| Number change (calculator) | Digits roll/crossfade via `AnimatedContent` with `slideInVertically + fadeIn` | 150ms | EaseInOutQuad |
| Timer tick | Seconds digit subtle scale pulse (1.0 → 1.02 → 1.0) | 100ms | EaseOut |
| Phase transition | Current chip scales down + fades, next scales up + glows | 400ms | Spring(damping=0.6, stiffness=400) |
| Button press | Scale to 0.96 on press, spring back on release | 100ms down | EaseOut + spring |
| Chip selection | Fill color animated, border fades | 200ms | EaseInOut |
| Brew complete | Radial warm glow pulse + haptic | 600ms | EaseOut |
| Swipe to delete | Slide right + opacity fade + red background reveal | 250ms | EaseInCubic |

Use Compose animation APIs: `animateFloatAsState`, `AnimatedVisibility`, `AnimatedContent`, `Animatable`, `LaunchedEffect` with delay for stagger.

---

## Empty States

Every screen's empty state is the user's first impression. Make it count.

- Custom illustration for each screen: minimal line art in Primary color with gradient fills (coffee cup, grinder, beans, steam wisps). Not clip art, not emoji. Draw using Compose Canvas or simple vector drawables.
- Warm headline: e.g., "No beans in the memory yet"
- Helpful secondary text: e.g., "Tap + to save your first grind setting"
- CTA button uses the same gradient style as "Start Brew" — prominent, inviting

---

## Spacing & Touch Targets

- Minimum `16.dp` horizontal padding on all screen content
- `24.dp` vertical spacing between major sections
- All tappable elements: minimum `48.dp` height
- All buttons: minimum `56.dp` height
- Use spacing and elevation to create separation — avoid `1dp` divider lines. If a divider is truly needed, use `0.5dp` in Subtle Borders color.

---

## Self-Check Before Committing UI Code

Run through this checklist after generating any composable. If any answer is "no," fix it:

1. Are all background colors from the theme (not `#FFFFFF` or `#000000`)?
2. Are primary buttons using `Brush.horizontalGradient` (not flat `backgroundColor`)?
3. Are numbers displayed in DM Mono / JetBrains Mono (not Roboto)?
4. Are headers displayed in DM Sans (not Roboto)?
5. Do cards have `RoundedCornerShape(16.dp)` and a warm diffuse shadow?
6. Is horizontal padding at least `16.dp` on screen content?
7. Are all tappable elements at least `48.dp` tall?
8. Does every state change have an animation (no instant visibility swaps)?
9. Is there a designed empty state (illustration + headline + CTA) if this screen can have zero items?
10. Are there no Material 3 default purple/teal/blue colors anywhere?

---

## Anti-Patterns — Reject These Immediately

If Claude Code generates any of these, the output is wrong. Fix before committing:

- `#FFFFFF` as a screen background → use `#FAF7F2` light / `#121010` dark
- `#000000` as dark mode background → use `#121010` (unless AMOLED theme)
- Default Roboto on display text → use DM Sans / DM Mono
- Flat solid-color rectangle buttons → primary buttons must use gradients
- Gray placeholder empty states → use warm branded illustrations
- Any state change without animation → animate every transition
- Default Material 3 purple/teal/blue → use the warm color system above
- Harsh dark drop shadows → shadows must be warm and diffuse
- Cramped layouts under 16dp horizontal padding → add whitespace
- Touch targets under 48dp → increase size
- ALL CAPS text outside active timer phase labels → use sentence/title case
- `1dp` divider lines → use spacing/elevation or `0.5dp` in Subtle Borders color

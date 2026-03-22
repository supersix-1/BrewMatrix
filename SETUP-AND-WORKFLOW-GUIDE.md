# BrewMatrix + Claude Code: Setup & Workflow Guide

**This guide assumes you have zero coding experience.** It walks you through installing everything you need, placing the skill files where they belong, and the day-to-day workflow of building BrewMatrix with Claude Code.

---

## Part 1: What You Need Installed First

Before you can use Claude Code or build an Android app, you need three things on your computer:

### 1. Android Studio
This is the program that turns code into an Android app. Download it free from https://developer.android.com/studio and install it. During setup, say "yes" to installing the Android SDK (the toolkit Android apps need). Once installed, open it and let it finish downloading components — this takes a while the first time.

**Why you need it:** Claude Code writes the code, but Android Studio compiles it (turns it into an app you can run). It also includes an emulator — a fake phone on your screen you can test the app on.

### 2. Claude Code
This is Anthropic's command-line coding tool. It's the "builder" that will write your app's code based on your plain-English instructions.

**To install it**, open your terminal (on Mac: the Terminal app; on Windows: use WSL or the command prompt) and run:

```
curl -fsSL https://cli.claude.com/install.sh | sh
```

You'll need an Anthropic account with a Claude Pro, Max, or API subscription — Claude Code uses your account to work.

**Why you need it:** This is the tool that reads the skill files and actually writes the Kotlin code for your app.

### 3. Git
Git is version control — it saves snapshots of your project so you can undo mistakes. It's probably already installed on your Mac. On Windows, download it from https://git-scm.com.

**Why you need it:** The skill tells Claude Code to commit (save a snapshot) after every successful phase. If Phase 4 breaks something, you can roll back to the end of Phase 3 instead of starting over.

---

## Part 2: Setting Up Your Project

### Step 1: Create the Android project in Android Studio

1. Open Android Studio
2. Click **"New Project"**
3. Choose **"Empty Activity"** (the Compose one, not the old-style one)
4. Set these values:
   - **Name:** BrewMatrix
   - **Package name:** com.brewmatrix.app
   - **Language:** Kotlin
   - **Minimum SDK:** API 26 (Android 8.0)
5. Click **Finish** and wait for the project to load and sync

This creates the basic project structure that Claude Code will build on top of.

### Step 2: Initialize Git

Open a terminal, navigate to your project folder, and run:

```
cd ~/AndroidStudioProjects/BrewMatrix
git init
git add -A
git commit -m "Initial project setup from Android Studio"
```

This saves your starting point.

### Step 3: Install the BrewMatrix skill

The skill files you downloaded from our conversation need to go into a specific folder inside your project. Run these commands:

```
mkdir -p .claude/skills/brewmatrix-android/references
```

Then copy the two files into the right places:

```
cp /path/to/your/downloaded/SKILL.md .claude/skills/brewmatrix-android/SKILL.md
cp /path/to/your/downloaded/design-system.md .claude/skills/brewmatrix-android/references/design-system.md
```

Replace `/path/to/your/downloaded/` with wherever you saved the files from this conversation (probably your Downloads folder).

Your project should now look like this:

```
BrewMatrix/
├── .claude/
│   └── skills/
│       └── brewmatrix-android/
│           ├── SKILL.md                    ← Main skill file
│           └── references/
│               └── design-system.md        ← Visual design rules
├── app/
│   └── src/
│       └── ... (Android Studio generated files)
├── gradle/
│   └── libs.versions.toml                  ← Will be created by Claude Code
├── build.gradle.kts
└── settings.gradle.kts
```

### Step 4: Create a CLAUDE.md file (optional but recommended)

The CLAUDE.md file is like a sticky note that Claude Code reads at the start of every session. Create one in your project root:

```
touch CLAUDE.md
```

Open it in any text editor and paste this:

```markdown
# BrewMatrix

Android app built with Kotlin + Jetpack Compose.

## Key rules
- Use the brewmatrix-android skill for all work on this project
- All dependency versions go in gradle/libs.versions.toml
- Room uses KSP, not kapt
- Compose compiler is a Kotlin plugin (org.jetbrains.kotlin.plugin.compose)
- Run ./gradlew assembleDebug after every change to verify the build
- Commit to Git after every successful phase
```

This acts as a memory jog for Claude Code across sessions.

---

## Part 3: Your Day-to-Day Workflow

Here's what a typical work session looks like, step by step.

### Starting a session

1. **Open your terminal**
2. **Navigate to your project:**
   ```
   cd ~/AndroidStudioProjects/BrewMatrix
   ```
3. **Launch Claude Code:**
   ```
   claude
   ```
4. Claude Code starts up, reads your CLAUDE.md and discovers the skill files. You're ready to go.

### Talking to Claude Code

You type plain English. Claude Code reads your project files, writes code, and runs commands. Here's how a real session might go for Phase 1:

**You type:**
```
Let's start Phase 1 from the BrewMatrix skill. Set up the project with 
Compose, Hilt, Room with KSP, and Navigation. Create the theme files 
with the full color system. Add a bottom nav bar with four placeholder 
screens: Calculator, Timer, Grind Memory, and Brew Log.
```

**What Claude Code does:**
- Reads the skill automatically (because it matches the description)
- Reads the design-system.md reference (because it's creating UI)
- Plans which files to create/modify
- Explains the plan to you in plain English
- Starts writing code files, one at a time
- Modifies your build.gradle.kts and libs.versions.toml
- Creates theme files (Color.kt, Type.kt, etc.)
- Creates placeholder screens and navigation
- Runs `./gradlew assembleDebug` to verify it compiles
- Tells you the result

**You then:**
- Open Android Studio
- Click the green "Run" button (▶) to launch the app on the emulator
- Check that you see four tabs at the bottom and the app doesn't crash
- If it works, tell Claude Code: `That works. Commit this as Phase 1.`
- If something's wrong, describe what you see: `The app crashes when I tap the Timer tab. Here's the error: [paste the red text from Android Studio's Logcat panel]`

### Key commands you'll use

| What you type | What it does |
|---|---|
| Just describe what you want in English | Claude Code writes the code |
| `/clear` | Clears the conversation. Use between phases or when things get confusing. |
| `/compact` | Shrinks the conversation to save space when it gets long |
| `@filename` | Points Claude Code at a specific file (e.g., `@app/build.gradle.kts`) |

### The rhythm of each phase

Every build phase follows the same pattern:

1. **Tell Claude Code which phase to work on** — reference the skill by name or just describe the feature
2. **Claude Code plans** — it explains what it's going to build. Read the plan. If something sounds wrong, say so now.
3. **Claude Code builds** — it writes code, creates files, modifies Gradle configs
4. **Claude Code verifies** — it runs `./gradlew assembleDebug` to check for compile errors
5. **You test on the emulator** — run the app in Android Studio, tap around, check that things work
6. **You report back** — "Looks good" or "The ratio isn't calculating right, it shows 0 when I type 15"
7. **Claude Code fixes** — if there are issues, it patches them
8. **Commit** — once everything works, Claude Code commits to Git

### When things go wrong (they will)

**Build errors (the code won't compile):**
Claude Code usually catches these itself. If you see red text in Android Studio's "Build" panel, copy the entire error message and paste it to Claude Code. Don't try to interpret the error yourself — just give it the raw text.

**The app crashes when you tap something:**
Look at the bottom of Android Studio for a panel called "Logcat." It shows a wall of text — look for lines in RED. Copy those red lines and paste them to Claude Code. Say something like: "The app crashes when I tap the + button on the Grind Memory screen. Here's the error from Logcat:" and paste the text.

**Claude Code keeps going in circles:**
If it tries to fix something twice and it's still broken, say: "This isn't working. Let's clear context and try a fresh approach." Then type `/clear` and re-describe what you need, including what went wrong in the previous attempts. This is the three-strike rule from the skill.

**You don't understand what Claude Code built:**
Just ask. Say "Explain what that code does in plain English" or "What should I see when I run the app now?" Claude Code knows you're not a developer and will translate.

### Between sessions

When you close Claude Code and come back later (next day, next week), just:

1. Navigate to your project folder
2. Type `claude`
3. Tell it where you left off: "We finished Phase 3 last time. Let's start Phase 4 — the Grind Memory Matrix."

Claude Code will re-read your CLAUDE.md, discover the skill, and pick up where you left off. Your code is saved in the project files and your progress is saved in Git commits.

---

## Part 4: The Build Order (Quick Reference)

| Phase | What Gets Built | What You Test |
|---|---|---|
| 1 | Project skeleton, theme, bottom nav | App launches, 4 tabs visible, warm colors |
| 2 | Brew ratio calculator | Type coffee grams, water auto-calculates, presets work |
| 3 | Multi-phase brew timer | Timer counts down, screen stays on, phases auto-advance |
| 4 | Grind memory matrix | Add grinder + bean, save grind setting, delete works |
| 5 | Brew log | Finish a brew, rate it, see it in the log list |
| 6 | Settings + tip jar | Switch themes, tip jar buttons display correctly |
| 7 | Polish | Animations smooth, empty screens have illustrations, everything looks premium |

---

## Part 5: Tips From People Who've Done This Before

- **Don't skip phases or jump ahead.** Each phase builds on the previous one. If Phase 2 is broken, Phase 3 will be worse.
- **Commit often.** Every time Claude Code says "build successful," commit. You'll thank yourself later.
- **Use `/clear` liberally.** A fresh context window produces better code than a long, messy conversation.
- **Test on a real phone if you can.** The emulator is good, but some things (haptic feedback, screen wake lock, billing) only work properly on a real Android device. Connect your phone via USB, enable Developer Mode, and Android Studio will let you run the app directly on it.
- **Don't be afraid to say "that looks wrong."** If a screen looks ugly, if a color seems off, if an animation is jerky — tell Claude Code. Be specific: "The background is pure white, it should be the warm cream color" works better than "it looks bad."
- **Save your BrewMatrix Bible.** If you ever need to re-explain a complex requirement, you can paste the relevant section from the Bible directly into Claude Code.

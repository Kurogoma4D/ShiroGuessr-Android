---
name: shiro-gallery-design
description: >
  ShiroGuessr "Shiro Gallery" UI design guideline. Use when implementing or modifying UI
  for ShiroGuessr on either Android (Jetpack Compose) or iOS (SwiftUI). Defines design concept,
  color system, typography, screen-by-screen guidelines, animations, and component style guide.
  All UI changes MUST follow these guidelines.
---

# ShiroGuessr UI Design Guideline — "Shiro Gallery"

## 1. Current State Analysis

### Data Models (Shared Across Platforms)

| Model | Role |
| ----- | ---- |
| `RGBColor` | Represents subtle white colors with RGB values in the 245-255 range |
| `GameState` | Tracks the overall game state across 5 rounds (round list, current index, completion flag, total score) |
| `GameRound` | Per-round data (target color, selected color, distance, score, palette colors / pin info) |
| `GradientMap` | 50x50 bilinear-interpolated gradient map with 4 corner colors |
| `Pin` / `MapCoordinate` | Pin placement in Map mode using normalized coordinates (0.0-1.0) |
| `PaletteColor` | Wrapper for colors in the 5x5 Classic mode palette |

### Core Game Mechanics

- **Manhattan distance** measures color difference: `|r1-r2| + |g1-g2| + |b1-b2|` (range: 0-30)
- **Score**: `1000 * (1 - distance / 30)`, max 5,000 points across 5 rounds
- **Classic mode**: Pick the closest color from a 25-color palette
- **Map mode**: Place a pin on a gradient map to locate the target color within 60 seconds

### Problems with the Current UI

1. **Too generic**: Standard Material Design 3 app styling with no game-specific personality or excitement
2. **Plain start screen**: Just an icon + title + button; weak introduction to the game world
3. **No visual drama for colors**: Target and palette colors are "just rectangles" — no immersion into the game's core mechanic of perceiving subtle white differences
4. **Flat round transitions**: No tempo or tension between rounds
5. **Low-impact result screen**: Count-up animation exists but lacks visual celebration of achievement
6. **Overly utilitarian header**: Only a title and mode-toggle button; weak brand identity

---

## 2. Design Concept: "Shiro Gallery"

### Overview

The experience of viewing delicate white artworks in a gallery.
Players become "connoisseurs of white," navigating a space that is serene yet charged with the excitement of a game.

### Keywords

- **Refined Tension** — Elegance and suspense coexisting
- **Chromatic Intimacy** — An intimate dialogue with color
- **Quiet Drama** — Understated yet dramatic presentation

### Rationale

ShiroGuessr is a game about distinguishing whites. White stands out most beautifully against deep dark tones, within a refined gallery-like space. The core mechanic is "focus on subtle differences," so the UI should strip away unnecessary decoration and guide the eye toward color itself.

---

## 3. Color System

### Background Strategy: Dark Base

The current light theme (`#FCFCFF`) is a sound default for apps, but in a "distinguish whites" game it places white colors on a white background, making subtle differences hard to perceive. **Floating white color samples on a dark background** makes the differences stand out.

> **Important**: Dynamic color remains disabled. In a color-perception game, device-dependent color rendering is unacceptable.

### Color Palette

```css
/* Surface & Background */
--canvas-deep:       #0D0D12;    /* Main background — deep ink */
--canvas-elevated:   #1A1A22;    /* Card / panel background */
--canvas-subtle:     #252530;    /* Secondary surface */

/* Accent: Warm Gold (elegant warmth contrasting white) */
--accent-primary:    #C9A96E;    /* Gold accent — CTA, selection, score */
--accent-secondary:  #8B7A5E;    /* Muted gold — secondary UI elements */
--accent-container:  #2A2520;    /* Accent background */

/* Text */
--text-primary:      #E8E6E3;    /* Main text — slightly warm off-white */
--text-secondary:    #9995A0;    /* Secondary text */
--text-muted:        #5C5866;    /* Subdued text */

/* Feedback */
--score-high:        #7EC88B;    /* High score — muted green */
--score-mid:         #C9A96E;    /* Mid score — unified with accent */
--score-low:         #C87E7E;    /* Low score — muted red */
--timer-warning:     #D4956B;    /* Timer warning — orange */
--timer-critical:    #C87E7E;    /* Timer critical — red */

/* Color Sample Display */
--sample-border:     #3A3A45;    /* Border around color samples */
--sample-shadow:     rgba(0,0,0,0.4); /* Drop shadow for color samples */
```

### Coexistence with Neutral Framing

Color sample areas (target color, palette) are placed within a **neutral-gray mid-value frame** (around `#787880`). This prevents near-white colors from blending into the background while leveraging dark-background contrast.

---

## 4. Typography

### Font Selection Philosophy

Balance "elegance" and "readability." Use the contrast between serif and sans-serif to merge "classic prestige" with "modern game feel."

### Font Pairing

| Role | Font | Android | iOS | Size | Notes |
| ---- | ---- | ---- | ---- | ---- | ---- |
| Display (scores, large numbers) | **DM Serif Display** | Google Fonts dependency | Bundle | 48-72sp/pt | Serif elegance fits the gallery aesthetic. Render in gold (`#C9A96E`) |
| Headline (titles, headings) | **Outfit** (Bold/SemiBold) | Google Fonts dependency | Bundle | 24-32sp/pt | Geometric sans-serif — rounded yet sharp. Pairs well with gold accent. `letter-spacing: +0.02em` |
| Body (text, descriptions) | **Noto Sans JP** (Regular 400) | Google Fonts dependency | System font (Hiragino Sans) | 14-16sp/pt | Unified Japanese + Latin glyph coverage. iOS uses system font to reduce bundle size |
| Label (UI labels, buttons) | **Outfit** (Medium 500) | Google Fonts dependency | Bundle | 12-14sp/pt | `letter-spacing: +0.02em`. Never use ALL CAPS |
| Monospace (CSS values, distances) | **JetBrains Mono** | Bundle | Bundle | 12sp/pt | For result-screen values like `d=3` or `rgb(250,248,252)`. Use `text-muted` color |

### Alternative Display Fonts

- **Playfair Display** — More classically gallery-like. Italic variant available for expressive use
- **Cormorant Garamond** — More delicate and intellectual. Hard to read at small sizes; use for Display only

### Score Display: Special Treatment

Score numbers are a **hero element**. Render in DM Serif Display at 72sp/pt in gold accent. Use Tabular Figures (monospaced numerals) so digits don't shift horizontally during count-up animation.

---

## 5. Screen-by-Screen Design Guidelines

### 5.1 Start Screen

**Current**: Icon + title + tagline + button

**Improvements**:

- **Background**: Subtly animated white gradient over the dark canvas — an introduction to the game's world. A large, slowly shifting white radial gradient floating on the dark background
- **Title**: Display "ShiroGuessr" in headline font at large size. Highlight the "Shiro" portion with gold accent
- **Mode selection**: Replace single button with **two vertically stacked cards**
  - Classic: Palette icon illustration + "Find it among 25 colors"
  - Map: Gradient icon illustration + "Locate it in 60 seconds"
  - Cards should include preview elements conveying each mode's atmosphere
- **Transition**: Card tap -> card expands into the game screen (Shared Element Transition)

### 5.2 Game Screen Common: Header & Scoreboard

**Current**: Title text + mode-toggle button / round display + score text

**Improvements**:

- **Header**: Minimized. Only a small logo mark in the top-left. Hide mode toggle during gameplay; make it available only on start and result screens (prevents accidental taps mid-game)
- **Round indicator**: Replace text (`Round 3/5`) with **5 dot/step indicators**. Completed rounds are filled, current round is an animated ring, upcoming rounds are outlined
- **Score**: Persistent gold-colored number in the top-right. Bounce animation (scale spring) on point gain

### 5.3 Classic Mode: Target Color & Palette

**Current**: Label + rectangular color display + CSS string / 5x5 grid

**Improvements**:

#### Target Color Display

- **Shape**: Instead of a large rounded rectangle, use a **full-width banner**. Add thin `sample-border` lines above and below, framing it like a gallery exhibit
- **Label**: Replace verbose instruction text with a simple, muted "TARGET" in small English text. Let the color itself command attention
- **CSS value**: Hidden during gameplay (could serve as a hint). Shown only on the result screen

#### Color Palette

- **Layout**: Keep the 5x5 grid but **increase cell corner radius** (12dp -> 16dp) and slightly widen gaps (8dp -> 10dp)
- **Selection state**: Replace checkmark icon with a **gold ring animation**. Accent-colored border fades in around the selected cell with a subtle scale-up (1.05x)
- **Palette background**: Place on a dark surface (`canvas-elevated`) card, creating a clear boundary with color samples
- **Press effect**: Gentle sink on tap (0.95x scale -> return)

### 5.4 Map Mode: Gradient Map

**Current**: Canvas-rendered gradient + blue circle pin + red circle target pin

**Improvements**:

#### Gradient Map

- **Frame**: Wrap the map in a **gallery-style frame**. Dark border (3dp) on the outside, subtle shadow beyond that
- **Size**: Keep square aspect ratio but increase the ratio to screen width (the map is the star of this mode)
- **Pins**:
  - User pin: Gold accent circle + drop shadow. Ease-out drop animation on placement (150ms, replays on each placement)
  - Target pin: White circle + red outline. Pop-in + pulse animation on result reveal
- **Dashed line**: Change to gold color. Keep the draw animation

#### Timer

- **Display**: Larger font (one step up from current), centered at the top of the screen
- **Effects**: At 10 seconds remaining, timer digits start pulsing (scale oscillation). At 5 seconds, color shifts to red and pulse speed increases
- **Progress**: Add a thin linear progress bar below the timer digits, shrinking from left in sync with remaining time

### 5.5 Round Result Dialog

**Current**: ModalBottomSheet with color comparison + distance + score + star rating + continue button

**Improvements**:

- **Display method**: **ModalBottomSheet** with semi-transparent scrim (`Color.Black alpha 0.4`). Uses standard sheet open/close animations
- **Color comparison**: Target and selected colors shown as **large circles side by side**. "vs" text between them instead of an arrow
- **Distance display**: Large gold-colored number. Manhattan distance as the primary metric
- **Score display**: Visualize score out of 1000 with a progress ring (circular progress bar)
- **Star rating**: Add 1-5 star rating based on distance. Stars in gold; earned stars filled, unearned outlined
- **Continue action**: Sheet closes with slide-down animation before proceeding to next round

### 5.6 Final Result Screen

**Current**: Trophy icon + count-up score + round list + buttons

**Improvements**:

- **Background**: Subtle particle effect (high scores only). Small gold light particles floating
- **Score presentation**:
  - During count-up, digit color transitions based on score (gradient from low -> mid -> high)
  - On count-up completion, score-dependent burst effect (4000+: large burst, 3000+: small burst, below 2000: none)
  - Score digits in Display Large size, gold color
- **Round summary**:
  - Keep the horizontal row layout but add **staggered fade-in animation** for sequential reveal
  - Add star ratings to each row (1-5 stars, small, next to color circles)
  - Slightly enlarge color sample sizes to pop against the dark background
- **Action buttons**:
  - "Play Again": Gold filled button in the most prominent position
  - "Share" / "Copy": Unified as outlined buttons, side by side to save space

### 5.7 Tutorial

**Current**: 3-page HorizontalPager BottomSheet

**Improvements**:

- **Timing**: First launch only (unchanged)
- **Style**: BottomSheet -> **full-screen overlay** with card-style content on dark background
- **Illustrations**: Replace Material Icons with **simple diagrams** representing each page's concept
  - Page 1: Two white color samples side by side ("They look the same, but they're different")
  - Page 2: Diagram of tapping to select from a palette
  - Page 3: Diagram of placing a pin on a gradient map
- **Transitions**: Crossfade between pages

---

## 6. Animation & Micro-interactions

### Common Principles

- **Spring Animation**: For specific in-game interactions (target pin pop-in, score bounce). `stiffness: 300, dampingRatio: 0.7` (Android) / `response: 0.4, dampingFraction: 0.7` (iOS)
- **Tween/Ease**: For continuous transitions like color changes and fades. `300-500ms`, `EaseInOut`
- **Ease-Out**: For quick responsive feedback like pin placement. `150ms`, `EaseOut`
- **Minimal round transitions**: Round start displays content immediately without slide-in or fade-in animations. This keeps gameplay snappy and avoids unnecessary waiting between rounds
- **No excessive animation**: Must not interfere with gameplay. Palette selection and pin placement in particular must respond instantly

### Key Animation Inventory

| Scene | Animation | Priority |
| ----- | --------- | -------- |
| Game start | Card-to-game-screen transition | High |
| Round start | Content appears immediately (no transition animation) | — |
| Color selection (Classic) | Selection ring animation + subtle scale | Medium |
| Pin placement (Map) | Ease-out drop (150ms, replays on each placement) | Medium |
| Guess submission | Button ripple + brief freeze (100ms) -> result sheet | High |
| Target pin reveal (Map) | Pop-in + pulse | High |
| Dashed line draw (Map) | Line-extending animation (keep existing) | Medium |
| Score count-up | Digit count-up + color transition | High |
| Round result display | ModalBottomSheet standard slide-up / slide-down | High |
| Final result rows | Staggered fade-in (100ms intervals) | Medium |
| Particles (high score) | Floating gold light particles | Low |

---

## 7. Component Style Guide

### Buttons

```yaml
Filled (Primary CTA):
  background: accent-primary (#C9A96E)
  text: canvas-deep (#0D0D12)
  corner-radius: 24dp
  press: scale(0.97) + darken 10%
  shadow: 0 2dp 8dp rgba(0,0,0,0.3)

Outlined (Secondary):
  border: 1.5dp accent-secondary (#8B7A5E)
  text: accent-primary
  corner-radius: 24dp
  press: background accent-container + scale(0.97)

Text (Tertiary):
  text: text-secondary (#9995A0)
  press: text-primary + underline
```

### Cards / Panels

```yaml
background: canvas-elevated (#1A1A22)
corner-radius: 16dp
border: 1dp #2A2A35 (subtle border for separation)
shadow: 0 4dp 16dp rgba(0,0,0,0.4)
```

### Color Sample Cells (Palette)

```yaml
corner-radius: 16dp
border-default: 1.5dp sample-border (#3A3A45)
border-selected: 2.5dp accent-primary (#C9A96E) with glow
shadow: 0 2dp 6dp rgba(0,0,0,0.3)
selected-scale: 1.05
```

### Round Indicator (Dots)

```yaml
size: 10dp
completed: fill accent-primary
current: ring accent-primary + pulse animation
upcoming: ring text-muted (#5C5866)
spacing: 12dp
```

---

## 8. Implementation Priority

### Phase 1: Color System & Theme Foundation (Both Platforms)

1. Define dark-based color system
2. Update theme files (`Theme.kt` / `ColorSystem.swift`)
3. Update typography
4. Update shared component styles (buttons, cards)

### Phase 2: Core Game Screen Improvements

1. Improve target color display (gallery-frame style)
2. Redesign color palette selection UI
3. Improve gradient map framing
4. Implement round dot indicators
5. Redesign score display

### Phase 3: Screen Transitions & Effects

1. Redesign start screen (mode selection cards)
2. Round result as ModalBottomSheet with slide-down close on Continue
3. Improve final result screen (star ratings, particles)
4. Splash screen: black background

### Phase 4: Polish

1. Redesign tutorial
2. Enhance timer effects
3. Fine-tune all animations
4. Accessibility review

---

## 9. Platform-Specific Notes

### Android (Jetpack Compose)

- Use `darkColorScheme()` as the base for `MaterialTheme`, providing custom colors via `CompositionLocal`
- Use `Animatable`, `animateXAsState`, `spring()` for animations
- Use `SharedTransitionLayout` (Compose 1.7+) for Shared Element Transitions
- Implement particles with `Canvas` Composable + `LaunchedEffect`

### iOS (SwiftUI)

- Replace color definitions in `ColorSystem.swift` with the new palette
- Switch to `.preferredColorScheme(.dark)`
- Use `.animation(.spring(...))` and `withAnimation` for animations
- Use `matchedGeometryEffect` for Shared Element Transitions
- Implement particles with `TimelineView` + `Canvas`

### Shared

- Color sample rendering must be **visually identical across both platforms** (verify no RGB rendering discrepancies)
- Dynamic color remains disabled
- No light theme is provided (white-color visibility is a core game requirement)

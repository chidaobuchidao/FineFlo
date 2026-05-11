# Loan-UI Ambient Data Enhancement Spec

> **Status**: Approved | **Base**: 2026-05-11-inclusive-finance-platform-design.md

## Design Direction: Ambient Data
Gradient accents, radial glow orbs, atmospheric depth. Dark + Eye-Care Light + System themes.

## Enhancements (14 items)

### Theme System
- 3 modes: Dark (#0B0F19) / Eye-Care Light (#F8FAFC) / Follow System
- `data-theme` attribute + CSS vars + Pinia store + localStorage + prefers-color-scheme
- TopBar segmented-control toggle

### Navigation
- Sidebar collapse 260px↔72px with spring animation
- SVG icon system replacing emoji (20+ icons)
- Brand collapses to logo-only in compact mode

### Animation System (Apple-style)
- CSS spring() for micro-interactions (press, hover, focus)
- GSAP for orchestrated sequences (page transitions, staggered lists, number scroll)
- Spring curves: fast(0.3 100 8 0), standard(0.5 100 10 0), bouncy(0.3 100 3 0)
- Staggered entrances: cards 60ms, rows 40ms
- Press-down: scale(0.96) active state on interactive elements
- Page transitions: spring transform + opacity, out scales 1→0.97
- prefers-reduced-motion disables all non-essential animation

### Dashboard
- Gradient text accents per card (indigo, cyan, green, red)
- Radial glow orbs (positioned top-right of each stat card)
- Inline sparklines in stat cards
- Staggered card entrance (4 cards, 60ms stagger)

### DataTable
- Column sorting (click header toggles asc/desc/none)
- Shimmer skeleton loader (gradient animation, replaces pulse)
- Glow border hover rows
- Row entrance stagger (40ms each)
- Sticky header

### States
- Shimmer skeleton on tables/cards during loading
- Contextual empty states with icon + message per view
- Error banners with retry button
- Toast notification system (already exists, enhance with spring entrance)

### Charts
- Custom ECharts dark/light theme (centralized, gradient colors)
- ChartPanel: fullscreen toggle, export PNG button
- Responsive resize (already exists)

### Login
- Password visibility toggle
- Animated card entrance (spring scale 0.92→1)
- Per-field validation errors

### Misc
- Search with 300ms debounce
- Focus-visible ring utility
- Reduced motion media query
- SVG icon system (20+ icons)

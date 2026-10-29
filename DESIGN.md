# Design System — Employee Leave Management System (ELMS)

## 1. Design Philosophy & System Identity
- **Product Truth**: Enterprise HR Leave Management & Operations System for corporate workforce administration.
- **Mode**: Operate — Professional enterprise B2B dashboard, scanability, structured audit trails.
- **Color Theme**: Slate Steel Palette (`#111522` → `#DCE3EF`) integrated with enterprise surfaces.
- **Craft Floor Rules**: Zero gradient text, zero pill buttons (crisp 2px-4px radius), full browser surface theming (custom scrollbar, selection, focus ring, caret, tabular nums), minimum functional font size of 12px.

## 2. Color Palette & Surfaces
```css
:root {
  /* Surfaces */
  --color-bg: #f8fafc;
  --color-surface: #ffffff;
  --color-surface-subtle: #f1f5f9;
  --color-border: #e2e8f0;
  --color-border-strong: #cbd5e1;

  /* Enterprise Slate Accents (#111522 → #DCE3EF ramp) */
  --color-navy-950: #111522;      /* Deepest header background */
  --color-navy-900: #1E263B;      /* App header / belt */
  --color-navy-800: #303D59;      /* Brand badge / active tab */
  --color-navy-700: #536684;      /* Dividing borders */
  --color-navy-secondary: #98A9C4;
  --color-text-inverted: #DCE3EF; /* Header text */

  /* Body Text */
  --color-text-primary: #0f172a;
  --color-text-secondary: #475569;
  --color-text-muted: #64748b;

  /* Cobalt Primary Action */
  --color-primary: #2563eb;
  --color-primary-hover: #1d4ed8;

  /* Semantic Statuses */
  --color-success: #059669;
  --color-warning: #d97706;
  --color-danger: #dc2626;
  --color-info: #2563eb;
}
```

## 3. Typography
- **Stack**: High-Performance System UI Stack (`-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto...`)
- **Data & Tables**: `font-variant-numeric: tabular-nums` for dates, balances, leave counts, employee IDs.
- **Scale**:
  - Root: 16px
  - Metrics: 1.75rem (700 weight)
  - Headings: 1.15rem – 1.4rem
  - Body: 0.875rem (14px)
  - Badges & Meta: 0.75rem (12px, WCAG legibility compliant)

## 4. Components & Elevation
- **Data Tables**: Striped rows (`#fafbfc`), hover row tint (`rgba(37, 99, 235, 0.03)`), 1px solid crisp border dividers, tabular numbers.
- **Status Badges**: Crisp 2px-3px rectangular radius, semantic background and border pairing (approved, pending, rejected, conflict).
- **Buttons**: Crisp 4px rectangular radius (`--radius-md`). Zero pills.
- **Header**: 56px enterprise dark belt (`var(--color-navy-900)`), subtle 1px border.

## 5. Browser Surfaces
- `::selection`: `rgba(83, 102, 132, 0.35)` with `#111522`
- `input, textarea, select`: `caret-color: #2563eb`
- `:focus-visible`: `2px solid #2563eb`, outline offset 2px
- `::-webkit-scrollbar`: 7px width, `#cbd5e1` thumb with `#f8fafc` track

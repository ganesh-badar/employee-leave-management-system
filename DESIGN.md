# Design System — Employee Leave Management System (ELMS)

## 1. Design Philosophy & System Identity
- **Product Truth**: Enterprise HR Leave Management & Operations System for corporate workforce administration.
- **Mode**: Operate — Professional enterprise B2B dashboard, scanability, structured audit trails, tabular dense layouts.
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

  /* Enterprise Slate Accents */
  --color-navy-950: #070809;
  --color-navy-900: #0e1015;
  --color-navy-800: #131620;
  --color-navy-700: #1e2536;

  /* Typography */
  --color-text-primary: #0f172a;
  --color-text-secondary: #475569;
  --color-text-muted: #64748b;
  --color-text-inverted: #f1f5f9;

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
- **Display**: `'Space Grotesk', system-ui, sans-serif` for metric totals, card titles, and section headers (-0.02em letter-spacing).
- **Body**: `'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif` for data grid, forms, navigation.
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
- `::selection`: `rgba(37, 99, 235, 0.25)` with `#0f172a`
- `input, textarea, select`: `caret-color: #2563eb`
- `:focus-visible`: `2px solid #2563eb`, outline offset 2px
- `::-webkit-scrollbar`: 7px width, `#cbd5e1` thumb with `#f8fafc` track


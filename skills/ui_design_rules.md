# Skill: UI Design Rules and Brand Guidelines

## Metadata
- **ID**: `ui_design_rules_skill`
- **Description**: Defines visual guidelines, typography, layout patterns, and CSS rules using the pastel periwinkle-mint color palette.
- **Color Palette**:
  - Primary Accent: `#9FA1FF` (Periwinkle Blue)
  - Secondary Accent: `#B5BAFF` (Light Periwinkle)
  - Sky Blue (Subtle highlights): `#AEE2FF` (Soft Sky Blue)
  - Mint Green (Success/Active): `#D9F9DF` (Soft Mint Green)
  - Background Neutral: `#F8F9FD` (Soft Cool White)
  - Text Neutral: `#2E3A59` (Deep Slate Blue)

## Layout & Styling Rules

### 1. Typography
- **Font Family**: Use Google Font `Outfit` or `Inter` as the primary typeface.
- **Font Scale**:
  - H1 Header: `2.25rem`, semi-bold (600), line-height `1.2`.
  - Body: `1rem`, regular (400), line-height `1.5`.

### 2. Cards & Glassmorphism
- **Design Pattern**: Components should reside in white container cards with rounded corners (`border-radius: 16px`) and subtle drop shadows (`box-shadow: 0 10px 30px rgba(159, 161, 255, 0.1)`).
- **Background**: Solid `#FFFFFF` or glassmorphic gradients.

### 3. Color Usage & Status Indicators
- **Active State (Badge)**: Background `#D9F9DF` with text `#1E4E2C` (Deep Green).
- **Inactive State (Badge)**: Background `#FFE6E6` with text `#8B0000` (Deep Red).
- **Primary Buttons / Links**: Background `#9FA1FF`, color `#FFFFFF`. Smooth hover transition to `#B5BAFF` with scale animation (`transform: translateY(-2px)`).
- **Text Color**: Deep slate `#2E3A59` for readable high-contrast copy, and `#8F9BB3` for secondary/disabled text.

### 4. Interactive Components (Tables & Forms)
- **Table Design**: Remove borders (`border: none`). Use alternating row backgrounds, round headers, hover actions, and cell paddings of `16px 20px`.
- **Form Inputs**: Outline style with `border: 2px solid #E4E9F2`. Focused state transitions to `border-color: #9FA1FF` with a soft blue shadow glow.

---

## Success Criteria for UI Implementation
- Responsive design adapting gracefully to mobile, tablet, and desktop screens.
- Zero raw HTML components (all elements styled with premium CSS rules).
- Micro-animations added on hover, active state, and transition.

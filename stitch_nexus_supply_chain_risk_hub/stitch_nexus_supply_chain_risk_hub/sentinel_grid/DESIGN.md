# Design System Specification: The Kinetic Intelligence Framework

## 1. Overview & Creative North Star: "The Neon Sentinel"
The supply chain is no longer a linear path; it is a live, breathing organism. This design system moves away from the static, "dashboard-in-a-box" aesthetic of legacy SaaS. Our Creative North Star is **The Neon Sentinel**: a high-fidelity, command-center experience that feels both authoritative and ethereal.

By leveraging **True Dark Mode** and **Glassmorphism**, we break the traditional grid. The UI shouldn't feel like a series of flat containers, but rather a digital HUD (Heads-Up Display) floating over a deep, infinite abyss. We utilize intentional asymmetry—placing high-density data visualizations against expansive, "breathing" negative space—to guide the user’s eye toward disruptions before they become disasters.

---

## 2. Colors: Tonal Depth & Luminous Accents
This system utilizes a sophisticated palette where "black" is never pure #000, and "lines" are virtually non-existent. 

### Core Palette
*   **Background (`#0d1322`):** The infinite base. All navigation and layout stem from this depth.
*   **Surface (`#111B2E`):** The primary staging area for content.
*   **Primary Accent (`#06B6D4` - Electric Cyan):** Used sparingly for critical interactive paths and the "Neon Glow" effect.

### The "No-Line" Rule
To achieve a premium editorial feel, **standard 1px solid borders are prohibited** for sectioning. Boundaries must be defined through:
1.  **Background Shifts:** Use `surface_container_low` against `surface` to define regions.
2.  **Tonal Transitions:** Defining a space by its change in luminosity rather than a stroke.
3.  **The Ghost Border:** If containment is required for accessibility, use `outline_variant` at **10% opacity**.

### Surface Hierarchy & Nesting
Treat the UI as layered sheets of obsidian glass. 
*   **Lowest Level:** `surface_container_lowest` for background utility areas.
*   **Mid Level:** `surface_container` for the main workspace.
*   **Highest Level:** `surface_container_highest` for active modals or hovering cards.

### Signature Textures
Apply a subtle linear gradient to Primary CTAs: `primary` (top-left) to `primary_container` (bottom-right). This adds "soul" and prevents the Electric Cyan from appearing flat or "cheap."

---

## 3. Typography: The Editorial Command
We use **Inter** for its mathematical precision. The hierarchy is designed to oscillate between high-density data and bold, atmospheric headers.

*   **Display & Headlines:** Used for "at-a-glance" status updates. High contrast against the dark background.
*   **Section Headers (The Signature Look):** Always **ALL-CAPS** with `letter-spacing: 0.15em`. This creates an authoritative, military-grade navigational feel.
*   **Body & Labels:** Optimized for legibility in low-light environments. Use `on_surface_variant` for secondary metadata to reduce eye strain.

| Role | Token | Size | Tracking | Case |
| :--- | :--- | :--- | :--- | :--- |
| Section Header | `label-md` | 0.75rem | 0.15em | All-Caps |
| Global Status | `display-md` | 2.75rem | -0.02em | Sentence |
| Body Text | `body-md` | 0.875rem | Normal | Sentence |

---

## 4. Elevation & Depth: Tonal Layering
In this design system, shadows are not black; they are glows or deep-tinted voids.

*   **The Layering Principle:** Avoid shadows on standard cards. Instead, place a `surface_container_high` card on a `surface_dim` background. The 16px (`lg`) corner radius provides the necessary softness.
*   **Glassmorphism:** For floating panels (e.g., disruption alerts), use `surface` at 60% opacity with a `backdrop-filter: blur(12px)`. This integrates the component into the environment.
*   **Ambient Shadows:** For elevated modals, use a wide-spread shadow (40px blur) using a 10% opacity version of `primary` (`#06B6D4`) to mimic the glow of a screen in a dark room.

---

## 5. Components: Precision Engineered

### The Sidebar (The Pulse)
*   **Style:** Deep navy, collapsible, using `surface_container_lowest`.
*   **Health Indicator:** A 4px pulse icon utilizing `secondary` (`#10B981`) with a CSS animation: `box-shadow: 0 0 10px #10B981`. It signifies the "System Online" status.

### Buttons
*   **Primary:** `primary` background with a subtle `0 0 15px` outer glow on hover. Text is `on_primary`.
*   **Secondary (Glass):** Transparent background, `outline_variant` ghost border (20% opacity), `primary` text.
*   **Tertiary:** No container. `primary` text. Underline appears only on hover.

### Input Fields
*   **Surface:** `surface_container_low`. 
*   **Active State:** The bottom border illuminates in `primary` (Electric Cyan) with a subtle 2px blur glow. **No full-box strokes.**

### Cards & Lists
*   **Rule:** **No Divider Lines.** Use vertical spacing (16px/24px) or subtle `surface` color shifts. 
*   **Interactive Cards:** On hover, the background shifts from `surface_container` to `surface_container_high` with a 200ms transition.

### Disruption Alerts (Custom Component)
*   A glassmorphic toast notification using the `error_container` (`#93000a`) as a background tint (20% opacity) with a heavy blur. This creates a "Red Alert" atmosphere without overwhelming the user with flat red boxes.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical layouts. Let a large data visualization bleed off the edge of a container.
*   **Do** use wide letter spacing for all metadata labels to enhance the "Technical/Bespoke" feel.
*   **Do** use the 16px (`lg`) border radius consistently across all containers and buttons.

### Don’t:
*   **Don’t** use pure white (`#FFFFFF`) for text. Use `on_surface` (`#dde2f8`) to prevent "halation" (the glowing bleed effect that makes text hard to read on dark backgrounds).
*   **Don’t** use solid 1px borders to separate content rows. Use `surface_container` hierarchy instead.
*   **Don’t** use standard "drop shadows." If it needs to float, it needs to glow or blur.
# Smart Supply Chain Disruption & Vendor Risk Management System

## 1. User Journey Map: Risk Analyst Workflow
**Scenario:** Resolving a High-Risk Supply Chain Alert
1.  **Discovery:** Analyst lands on the **Executive Dashboard**. They see a pulse in the "High Risk Count" KPI and a new "Rose Red" (Critical) alert in the live feed for "Micro-Chip Logistics Inc."
2.  **Investigation:** Analyst clicks the alert, which deep-links them to the **Risk Analysis & Evaluation** page for that specific vendor.
3.  **Analysis:** They review the massive Gauge Chart (showing a 0.85 risk) and the 90-day trend line. They notice a spike in "Defect Rate" in the interactive evaluation section.
4.  **Mitigation:** Analyst navigates to the **Alerts & Disruption** page. The system's AI panel recommends "Silicon Alpha" as a low-risk alternative with 98% capability match.
5.  **Resolution:** Analyst initiates a procurement request to the alternative vendor and clicks "Resolve Alert," adding a note about the transition.

## 2. Technical UI Architecture & Component Library
*   **Framework:** Tailwind CSS for rapid, utility-first styling of the dark-mode surfaces.
*   **Iconography:** Lucide-react or Phosphor Icons (Geometric, thin stroke).
*   **Charts:** Recharts or Highcharts for the glassmorphic gauge and spline visualizations.
*   **Components:** 
    *   **Surfaces:** Cards with `backdrop-filter: blur(12px)` and `border: 1px solid rgba(255, 255, 255, 0.1)`.
    *   **Data Tables:** Custom headless tables with sticky headers and status pill badges.
    *   **Feedback:** Toast notifications for real-time alert updates.

## 3. Design Tokens
*   **Background:** `#0B1120` (Deep Space)
*   **Surface:** `#111B2E` (Navy Slate)
*   **Primary:** `#06B6D4` (Electric Cyan)
*   **Critical:** `#F43F5E` (Rose Red)
*   **Warning:** `#F59E0B` (Amber)
*   **Success:** `#10B981` (Emerald)

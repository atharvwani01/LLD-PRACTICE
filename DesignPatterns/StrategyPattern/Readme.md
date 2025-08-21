# Strategy Pattern – Vehicles (Java) with Realistic Mini-Logic

This repo shows **why** Strategy helps by contrasting:
- A **bad** inheritance-only design (duplication, rigid hierarchy, closed for change).
- A **good** Strategy-based design (composition, reuse, runtime swapping, SRP).

Each `drive()` has tiny *real-ish* logic (4–5 lines): speed, traction/terrain, tire pressure, fuel burn.

---

## 🔎 TL;DR

- **Problem:** Behavior (“how to drive”) is baked into subclasses → duplication & class explosion.
- **Solution:** Extract driving behavior into **strategies** (`DriveStrategy`) and **compose** them into vehicles.
- **Wins:** No duplication, easy to extend, per-instance runtime swaps, clean tests.

---

## ❌ Without Strategy (Anti-Pattern)

### Problems Demonstrated
1. **Duplication:** `SportsVehicle` & `LuxuryVehicle` both carry the same “fast” logic.
2. **Rigid hierarchy:** To make one Passenger “fast”, you subclass or copy logic.
3. **Closed for change:** Evolving “fast” requires touching multiple classes.
4. **SRP broken:** Vehicle types also own driving algorithms.
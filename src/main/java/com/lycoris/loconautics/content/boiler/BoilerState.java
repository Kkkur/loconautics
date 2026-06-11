package com.lycoris.loconautics.content.boiler;

/**
 * The four operating states of the steam boiler multiblock.
 *
 * <pre>
 *   COLD    → fire just lit or boiler inactive. 20–30% SU output.
 *   WARMING → ramping up to pressure over ~90 seconds. Output scales linearly.
 *   HOT     → full operating pressure. 100% SU output.
 *   STARVED → coal or water exhausted. Output drops rapidly.
 *             If coal runs out the boiler cools back to COLD over time.
 *             If water runs out a steep SU penalty applies and the boiler
 *             takes damage (tracked via a damage counter on the controller BE).
 * </pre>
 */
public enum BoilerState {
    COLD,
    WARMING,
    HOT,
    STARVED;

    /** Returns true when the boiler is producing any output (not cold and not fully starved). */
    public boolean isActive() {
        return this == WARMING || this == HOT || this == STARVED;
    }

    /** Returns true when the boiler is at full operating pressure. */
    public boolean isHot() {
        return this == HOT;
    }
}
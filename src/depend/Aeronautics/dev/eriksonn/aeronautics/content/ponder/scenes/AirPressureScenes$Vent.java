/*
 * Decompiled with CFR 0.152.
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import dev.eriksonn.aeronautics.content.ponder.scenes.AirPressureScenes;

public static class AirPressureScenes.Vent
extends AirPressureScenes.Burner {
    @Override
    public String getSceneId() {
        return "vent_pressure";
    }

    @Override
    public String getSceneTitle() {
        return "Effect of Air Pressure on Steam Vents";
    }

    @Override
    public String getItemName() {
        return "Steam Vents";
    }
}

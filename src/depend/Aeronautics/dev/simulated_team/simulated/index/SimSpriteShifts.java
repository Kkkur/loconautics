/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SpriteShifter
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;

public class SimSpriteShifts {
    public static final SpriteShiftEntry ROPE_WINCH_COIL = SimSpriteShifts.get("block/rope_winch/winch_coil", "block/rope_winch/winch_coil_scroll");

    public static void init() {
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get((ResourceLocation)Simulated.path(originalLocation), (ResourceLocation)Simulated.path(targetLocation));
    }
}

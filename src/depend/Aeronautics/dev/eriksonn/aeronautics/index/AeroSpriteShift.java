/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.block.connected.AllCTTypes
 *  com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
 *  com.simibubi.create.foundation.block.connected.CTSpriteShifter
 *  com.simibubi.create.foundation.block.connected.CTType
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.resources.ResourceLocation;

public class AeroSpriteShift {
    public static final CTSpriteShiftEntry LEVITITE = AeroSpriteShift.omni("block/levitite");
    public static final CTSpriteShiftEntry PEARLESCENT_LEVITITE = AeroSpriteShift.omni("block/pearlescent_levitite");

    static CTSpriteShiftEntry omni(String name) {
        return CTSpriteShifter.getCT((CTType)AllCTTypes.OMNIDIRECTIONAL, (ResourceLocation)Aeronautics.path(name), (ResourceLocation)Aeronautics.path(name + "_connected"));
    }
}

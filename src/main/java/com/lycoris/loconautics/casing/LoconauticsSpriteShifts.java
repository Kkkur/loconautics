package com.lycoris.loconautics.casing;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import net.minecraft.resources.ResourceLocation;

public class LoconauticsSpriteShifts {

    public static final CTSpriteShiftEntry REINFORCED_CASING = getCT("reinforced_casing");

    private static CTSpriteShiftEntry getCT(String name) {
        return CTSpriteShifter.getCT(
                AllCTTypes.OMNIDIRECTIONAL,
                ResourceLocation.fromNamespaceAndPath("loconautics", "block/" + name),
                ResourceLocation.fromNamespaceAndPath("loconautics", "block/" + name + "_connected")
        );
    }
}
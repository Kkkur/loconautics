/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public record BlockEntityLiftingGasProvider.ClientBalloonInfo(int clientBalloonVolume, double clientBalloonFilled, double clientBalloonTarget, double clientBalloonLift, double clientBalloonChange, Vec3 gasCenter) {
    public static void writeToNBT(CompoundTag tag, ServerBalloon balloon) {
        if (balloon != null && balloon.getCenter() != null) {
            tag.putInt("Volume", balloon.getCapacity());
            tag.putDouble("Filled", balloon.getTotalFilledVolume());
            tag.putDouble("Target", balloon.getTotalTargetVolume());
            tag.putDouble("Delta", balloon.getTotalVolumeChange());
            tag.putDouble("Lift", balloon.getTotalLift());
            tag.putDouble("CenterX", balloon.getCenter().x);
            tag.putDouble("CenterY", balloon.getCenter().y);
            tag.putDouble("CenterZ", balloon.getCenter().z);
        }
    }

    public static BlockEntityLiftingGasProvider.ClientBalloonInfo readFromNBT(CompoundTag tag) {
        return new BlockEntityLiftingGasProvider.ClientBalloonInfo(tag.getInt("Volume"), tag.getDouble("Filled"), tag.getDouble("Target"), tag.getDouble("Lift"), tag.getDouble("Delta"), new Vec3(tag.getDouble("CenterX"), tag.getDouble("CenterY"), tag.getDouble("CenterZ")));
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  toni.sodiumextras.EmbyTools
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.sodiumextras;

import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import toni.sodiumextras.EmbyTools;

@Mixin(value={EmbyTools.class})
public class EmbyToolsMixin {
    @Overwrite
    public static boolean isEntityWithinDistance(BlockPos bePos, Vec3 camVec, int maxHeight, int maxDistanceSquare) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (double)bePos.getX() + 0.5, (double)bePos.getY() + 0.5, (double)bePos.getZ() + 0.5, camVec.x, camVec.y, camVec.z) < (double)maxDistanceSquare;
    }
}

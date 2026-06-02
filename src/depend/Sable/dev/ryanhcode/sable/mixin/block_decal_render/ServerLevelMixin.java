/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 */
package dev.ryanhcode.sable.mixin.block_decal_render;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value={ServerLevel.class})
public class ServerLevelMixin {
    @ModifyConstant(method={"destroyBlockProgress"}, constant={@Constant(doubleValue=1024.0, ordinal=0)})
    private double sable$blockDamageDistance(double originalBlockDamageDistanceConstant) {
        return Double.MAX_VALUE;
    }
}

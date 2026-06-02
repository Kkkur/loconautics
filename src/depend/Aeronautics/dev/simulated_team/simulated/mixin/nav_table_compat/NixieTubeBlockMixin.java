/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.nav_table_compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={NixieTubeBlock.class})
public class NixieTubeBlockMixin {
    @WrapOperation(method={"getPower"}, at={@At(value="FIELD", ordinal=1, target="Lnet/createmod/catnip/data/Iterate;directions:[Lnet/minecraft/core/Direction;")})
    private static Direction[] fixReadPower(Operation<Direction[]> original) {
        return new Direction[0];
    }
}

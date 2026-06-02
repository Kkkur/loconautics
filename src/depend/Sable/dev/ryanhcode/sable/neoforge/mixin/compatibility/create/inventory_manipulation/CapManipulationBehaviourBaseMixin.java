/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.inventory_manipulation;

import com.google.common.base.Predicate;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={CapManipulationBehaviourBase.class})
public class CapManipulationBehaviourBaseMixin {
    @Shadow
    protected Predicate<BlockEntity> filter;
    @Shadow
    protected boolean bypassSided;
    @Unique
    private BlockPos sable$caughtPos;

    @Redirect(method={"findNewCapability"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity sable$findNewCapOnSubLevel(Level level, BlockPos blockPos) {
        ActiveSableCompanion helper = Sable.HELPER;
        return helper.runIncludingSubLevels(level, blockPos.getCenter(), true, helper.getContaining(level, (Vec3i)blockPos), (subLevel, internalPos) -> {
            BlockEntity caughtBE = level.getBlockEntity(internalPos);
            if (this.filter.apply((Object)caughtBE)) {
                this.sable$caughtPos = internalPos;
                return caughtBE;
            }
            return null;
        });
    }

    @Redirect(method={"findNewCapability"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getCapability(Lnet/neoforged/neoforge/capabilities/BlockCapability;Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Ljava/lang/Object;"))
    public <T> T sable$redirectPos(Level instance, BlockCapability<T, Direction> blockCapability, BlockPos pos, Object dir, @Local BlockFace targetBlockFace) {
        return (T)instance.getCapability(blockCapability, this.sable$caughtPos, (Object)(this.bypassSided ? null : targetBlockFace.getFace()));
    }
}
